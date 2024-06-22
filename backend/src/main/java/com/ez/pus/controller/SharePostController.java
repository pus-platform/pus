package com.ez.pus.controller;

import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.dto.SharePostDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Notification;
import com.ez.pus.model.Post;
import com.ez.pus.model.SharePost;
import com.ez.pus.model.User;
import com.ez.pus.repository.PostRepository;
import com.ez.pus.repository.SharePostRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.service.NotificationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("/users/{userId}/shared-posts")
@RequiredArgsConstructor
public class SharePostController {

    private static final Logger log = LoggerFactory.getLogger(SharePostController.class);

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SharePostRepository sharePostRepository;
    private final NotificationService notificationService;
    private final SharePostModelAssembler assembler;

    @Component
    public static class SharePostModelAssembler
            implements RepresentationModelAssembler<SharePost, EntityModel<SharePostDTO>> {
        @Override
        public @NonNull EntityModel<SharePostDTO> toModel(@NonNull SharePost sharePost) {
            return EntityModel.of(SharePostDTO.fromSharePost(sharePost),
                    linkTo(methodOn(SharePostController.class).getSharedPost( sharePost.getUser().getId(), sharePost.getPost().getId())) .withSelfRel(),
                    linkTo(methodOn(PostController.class).one(sharePost.getPost().getId())).withRel("Post"),
                    linkTo(methodOn(UserController.class).one(sharePost.getUser().getId())).withRel("User"));
        }
    }

    @GetMapping("{postId}")
    public EntityModel<SharePostDTO> getSharedPost(@PathVariable Long userId, @PathVariable Long postId) {
        log.trace("Entering getSharedPost()");
        log.debug("Fetching user with id: {}", userId);
        log.info("Request to get shared post: userId={}, postId={}", userId, postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "ID",
                            userId);
                });
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found with id: {}", postId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                            postId);
                });
        SharePost sharePost = sharePostRepository.findByUserAndPost(user, post)
                .orElseThrow(() -> {
                    log.error("Post not shared by user");
                    return new RuntimeException("Post not shared by user");
                });
        log.info("Shared post found: userId={}, postId={}", userId, postId);
        log.info("Exiting getSharedPost()");
        return assembler.toModel(sharePost);
    }

    @PostMapping("{postId}")
    public ResponseEntity<EntityModel<SharePostDTO>> sharePost(@PathVariable Long userId, @PathVariable Long postId,
                                                               @RequestBody SharePostDTO sharePostDTO) {
        log.trace("Entering sharePost()");
        log.debug("Fetching user with id: {}", userId);
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot add shared post to another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot add shared post to another user");
        }
        log.info("Request to share post: userId={}, postId={}", userId, postId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "ID",
                            userId);
                });
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Post not found with id: {}", postId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                            postId);
                });
        SharePost sharePost = SharePost.builder()
                .sharedAt(sharePostDTO.getSharedAt())
                .build();
        sharePost.setUser(user);
        sharePost.setPost(post);
        notificationService.setShare(sharePost);
        notificationService.sendNotification(NotificationType.POST_SHARE);

        // Real-time notification
        notificationService.sendRealTimeNotification(NotificationDTO.fromNotification(
                Notification.builder()
                        .notificationType(NotificationType.POST_SHARE)
                        .content(user.getUsername() + " shared your post")
                        .user(post.getUser())
                        .notifiedAt(LocalDateTime.now())
                        .build()
        ));

        log.debug("Sharing post: {}", sharePost);
        SharePost savedSharePost = sharePostRepository.save(sharePost);
        EntityModel<SharePostDTO> entityModel = assembler.toModel(savedSharePost);
        log.info("Post shared: userId={}, postId={}", userId, postId);
        log.info("Exiting sharePost()");
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{sharePostId}")
    public ResponseEntity<EntityModel<SharePostDTO>> updateSharedPost(@PathVariable Long userId,
                                                                      @PathVariable Long sharePostId,
                                                                      @RequestBody SharePostDTO newSharePost) {
        log.trace("Entering updateSharedPost()");

        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot edit shared post of another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot edit shared post of another user");
        }
        log.debug("Fetching user with id: {}", userId);
        userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "ID",
                            userId);
                });

        log.debug("Fetching sharePost with id: {}", sharePostId);
        SharePost sharePost = sharePostRepository.findById(sharePostId)
                .orElseThrow(() -> {
                    log.error("Shared post not found with id: {}", sharePostId);
                    return new RuntimeException("Shared post not found with id: " + sharePostId);
                });

        log.info("Updating shared post for user: {}, with shared post ID: {}", userId, sharePostId);

        SharePost updatedSharePost = sharePostRepository.save(sharePost);
        EntityModel<SharePostDTO> entityModel = assembler.toModel(updatedSharePost);

        log.info("Shared post updated: userId={}, sharePostId={}", userId, sharePostId);
        log.trace("Exiting updateSharedPost()");

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deleteSharedPost(@PathVariable Long userId, @PathVariable Long postId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot delete shared post of another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete shared post of another user");
        }
        log.trace("Entering deleteSharedPost()");
        log.debug("Request to delete shared post: userId={}, postId={}", userId, postId);
        sharePostRepository.deleteById(postId);
        log.info("Shared post deleted: postId={}", postId);
        log.info("Exiting deleteSharedPost()");
        log.info("Shared post deletion process complete");
        return ResponseEntity.noContent().build();
    }
}
