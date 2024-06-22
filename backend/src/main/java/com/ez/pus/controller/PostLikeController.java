package com.ez.pus.controller;

import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.dto.PostLikeDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Notification;
import com.ez.pus.model.PostLike;
import com.ez.pus.repository.PostLikeRepository;
import com.ez.pus.repository.PostRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.service.NotificationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RequiredArgsConstructor
@RestController
public class PostLikeController {
    private static final Logger logger = LoggerFactory.getLogger(PostLikeController.class);
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final NotificationService notificationService;

    @Component
    private static class PostLikeModelAssembler implements RepresentationModelAssembler<PostLike, EntityModel<PostLikeDTO>> {

        @Override
        public @NonNull EntityModel<PostLikeDTO> toModel(@NonNull PostLike postLike) {
            logger.debug("Assembling model for PostLike with Post ID: {} and User ID: {}",
                    postLike.getPost().getId(),
                    postLike.getUser().getId());
            return EntityModel.of(PostLikeDTO.fromPostLike(postLike),
                    linkTo(methodOn(PostLikeController.class).getLike(postLike.getPost().getId(),
                            postLike.getUser().getId())).withSelfRel(),
                    linkTo(methodOn(PostLikeController.class).getAllLikesForPost(
                            postLike.getPost().getId(), 0, 3,
                            "likedAt")).withRel("postLikes"),
                    linkTo(methodOn(PostController.class).one(postLike.getPost().getId()))
                            .withRel("post"),
                    linkTo(methodOn(UserController.class).one(postLike.getUser().getId()))
                            .withRel("user"));
        }
    }

    @GetMapping("/posts/{postId}/likes/{userId}")
    public EntityModel<PostLikeDTO> getLike(@PathVariable Long postId, @PathVariable Long userId) {
        logger.trace("Fetching like for post ID: {} and user ID: {}", postId, userId);
        PostLike.PostLikeId id = new PostLike.PostLikeId(postId, userId);
        PostLike postLike = postLikeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Like not found for post ID: {} and user ID: {}", postId, userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("PostLike", "ID",
                            postId);
                });
        logger.info("Like fetched successfully for post ID: {} and user ID: {}", postId, userId);
        return new PostLikeModelAssembler().toModel(postLike);
    }

    @GetMapping("/posts/{postId}/likes")
    public CollectionModel<EntityModel<PostLikeDTO>> getAllLikesForPost(@PathVariable Long postId,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        @RequestParam(defaultValue = "3") int size,
                                                                        @RequestParam(defaultValue = "likedAt") String sort) {
        logger.debug("Fetching all likes for post ID: {}", postId);
        List<PostLike> postLikes = postLikeRepository.findByPostId(postId,
                PageRequest.of(page, size, Sort.by(sort).descending()));
        if (postLikes.isEmpty()) {
            logger.warn("No likes found for post ID: {}", postId);
            return CollectionModel.of(List.of());
        }
        logger.info("Likes fetched for post ID: {}", postId);
        List<EntityModel<PostLikeDTO>> likes = postLikes.stream()
                .map(new PostLikeModelAssembler()::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(likes);
    }

    @PostMapping("/posts/{postId}/likes")
    public ResponseEntity<EntityModel<PostLikeDTO>> addLike(@Valid @RequestBody PostLikeDTO postLikeDTO) {
        logger.trace("Adding like for post ID: {}", postLikeDTO.getPost());
        PostLike postLike = PostLike.builder()
                .reaction(postLikeDTO.getReaction())
                .post(postRepository.findById(postLikeDTO.getPost().getId()).orElseThrow(() ->
                        new RuntimeException("Post not found")))
                .likedAt(postLikeDTO.getLikedAt())
                .build();
        postLike.setUser(
                userRepository.findById(
                        userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())
                ).orElseThrow(() -> new RuntimeException("User not found"))
        );
        PostLike newPostLike = postLikeRepository.save(postLike);
        logger.info("Like added for post ID: {}", postLike.getPost().getId());
        notificationService.setPostLike(newPostLike);
        notificationService.sendNotification(NotificationType.POST_LIKE);

        // Real-time notification
        notificationService.sendRealTimeNotification(NotificationDTO.fromNotification(
                Notification.builder()
                        .notificationType(NotificationType.POST_LIKE)
                        .content(postLike.getUser().getUsername() + " reacted your post")
                        .user(postLike.getPost().getUser())
                        .notifiedAt(LocalDateTime.now())
                        .build()
        ));

        return ResponseEntity
                .created(linkTo(methodOn(PostLikeController.class).getLike(
                        newPostLike.getPost().getId(),
                        newPostLike.getUser().getId())).withSelfRel().toUri())
                .body(new PostLikeModelAssembler().toModel(newPostLike));
    }

    @PutMapping("/posts/{postId}/likes/{userId}")
    public ResponseEntity<?> edit(@Valid @RequestBody PostLikeDTO likeDTO, @PathVariable Long postId, @PathVariable Long userId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            logger.error("You cannot edit like of another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot edit like of another user");
        }
        logger.debug("Editing like for post ID: {} and user ID: {}", postId, userId);
        PostLike.PostLikeId id = new PostLike.PostLikeId(postId, userId);
        PostLike edited = postLikeRepository.findById(id)
                .map(postLike -> {
                    postLike.setReaction(likeDTO.getReaction());
                    return postLikeRepository.save(postLike);
                })
                .orElseGet(() -> postLikeRepository.save(PostLike.builder()
                        .reaction(likeDTO.getReaction())
                        .post(postRepository.findById(postId).get())
                        .user(userRepository.findById(userId).get())
                        .build()
                ));
        logger.info("Like edited for post ID: {} and user ID: {}", postId, userId);
        return ResponseEntity
                .created(linkTo(
                        methodOn(PostLikeController.class).getLike(edited.getPost().getId(),
                                edited.getUser().getId()))
                        .withSelfRel().toUri())
                .body(new PostLikeModelAssembler().toModel(edited));
    }

    @DeleteMapping("/posts/{postId}/likes/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long postId, @PathVariable Long userId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            logger.error("You cannot delete like of another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete like of another user");
        }
        logger.trace("Deleting like for post ID: {} and user ID: {}", postId, userId);
        PostLike.PostLikeId id = new PostLike.PostLikeId(postId, userId);
        PostLike postLike = postLikeRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Like not found for post ID: {} and user ID: {}", postId, userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                            postId);
                });
        postLikeRepository.delete(postLike);
        logger.info("Like deleted for post ID: {} and user ID: {}", postId, userId);
        return ResponseEntity.noContent().build();
    }
}
