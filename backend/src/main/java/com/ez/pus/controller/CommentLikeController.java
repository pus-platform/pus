package com.ez.pus.controller;

import com.ez.pus.dto.CommentLikeDTO;
import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.dto.ShortUserDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Comment;
import com.ez.pus.model.CommentLike;
import com.ez.pus.model.Notification;
import com.ez.pus.model.User;
import com.ez.pus.repository.CommentLikeRepository;
import com.ez.pus.repository.CommentRepository;
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
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = { "http://localhost:5173", "http://localhsot:3000" })
@RestController
@RequestMapping("/posts/{postId}/comments/{commentId}/likes")
@RequiredArgsConstructor
public class CommentLikeController {

    private static final Logger log = LoggerFactory.getLogger(CommentLikeController.class);
    @Component
    static class CommentLikeModelAssembler
            implements RepresentationModelAssembler<CommentLike, EntityModel<CommentLikeDTO>> {

        @NonNull
        @Override
        public EntityModel<CommentLikeDTO> toModel(@NonNull CommentLike commentLike) {
            log.trace("Assembling model for comment like by user ID: {}", commentLike.getUser().getId());
            return EntityModel.of(CommentLikeDTO.fromCommentLike(commentLike),
                    linkTo(methodOn(CommentLikeController.class).one(
                            commentLike.getComment().getPost().getId(),
                            commentLike.getComment().getId(),
                            commentLike.getUser().getId())).withSelfRel(),
                    linkTo(methodOn(CommentLikeController.class).all(
                            commentLike.getComment().getPost().getId(),
                            commentLike.getComment().getId(), 0, 3, "likedAt"))
                            .withRel("comment likes"),
                    linkTo(methodOn(CommentController.class).one(commentLike.getComment().getId()))
                            .withRel("comment"),
                    linkTo(methodOn(UserController.class).one(commentLike.getUser().getId()))
                            .withRel("user"));
        }
    }

    private final CommentLikeRepository repository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final CommentLikeModelAssembler assembler;

    @GetMapping("/{userId}")
    EntityModel<CommentLikeDTO> one(@PathVariable Long postId, @PathVariable Long commentId,
                                    @PathVariable Long userId) {
        log.debug("Fetching comment like for comment ID: {} and user ID: {}", commentId, userId);
        CommentLike commentLike = repository.findByUserIdAndCommentId(userId, commentId)
                .orElseThrow(() -> {
                    log.error("Comment like not found for comment ID: {} and user ID: {}",
                            commentId, userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Comment Like",
                            "ID", userId);
                });
        log.info("Comment like retrieved for comment ID: {} and user ID: {}", commentId, userId);
        return assembler.toModel(commentLike);
    }

    @GetMapping
    CollectionModel<EntityModel<CommentLikeDTO>> all(@PathVariable Long postId, @PathVariable Long commentId,
                                                     @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
                                                     @RequestParam(defaultValue = "likedAt") String sort) {
        log.debug("Listing all likes for comment ID: {} with page: {}, size: {}, sort: {}", commentId, page,
                size, sort);
        List<CommentLike> nextPage = repository.findAllByCommentId(commentId,
                PageRequest.of(page + 1, size, Sort.by(sort)));
        CollectionModel<EntityModel<CommentLikeDTO>> likes = CollectionModel.of(
                        repository.findAllByCommentId(commentId,
                                        PageRequest.of(page, size, Sort.by(sort).descending()))
                                .stream()
                                .map(assembler::toModel)
                                .toList())
                .addIf(page > 0,
                        () -> linkTo(methodOn(CommentLikeController.class).all(postId,
                                commentId, page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(CommentLikeController.class).all(postId, commentId, page, size,
                        sort))
                        .withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(CommentLikeController.class).all(postId,
                                commentId, page + 1, size, sort))
                                .withRel("Next Page"));
        log.info("Likes listed for comment ID: {}", commentId);
        if (likes.getContent().isEmpty()) {
            return CollectionModel.of(List.of());
        }
        return likes;
    }

    @PutMapping("/{userId}")
    ResponseEntity<?> edit(@Valid @RequestBody CommentLikeDTO likeDTO, @PathVariable Long commentId,
                           @PathVariable Long userId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot edit like for someone else");
            throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
        }
        log.debug("Editing like for comment ID: {} and user ID: {}", commentId, userId);
        CommentLike edited = repository.findById(new CommentLike.CommentLikeId(commentId, userId))
                .map(commentLike -> {
                    commentLike.setReaction(likeDTO.getReaction());
                    log.info("Like updated for comment ID: {} and user ID: {}", commentId, userId);
                    return repository.save(commentLike);
                })
                .orElseGet(() -> {
                    log.info("Adding new like for comment ID: {} and user ID: {}", commentId,
                            userId);
                    return repository.save(CommentLike.builder()
                            .comment(commentRepository.findById(likeDTO.getComment()).get())
                            .user(userRepository.findById(likeDTO.getUser().getId()).get())
                            .reaction(likeDTO.getReaction())
                            .build());
                });
        EntityModel<CommentLikeDTO> entityModel = assembler.toModel(edited);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody CommentLikeDTO newCommentLikeDTO) {
        log.debug("Adding new like for comment ID: {}", newCommentLikeDTO.getComment());
        newCommentLikeDTO.setUser(ShortUserDTO.builder()
                .id(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())).build());
        User user = userRepository.findById(newCommentLikeDTO.getUser().getId())
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("User", "ID", newCommentLikeDTO.getUser().getId()));
        Comment comment = commentRepository.findById(newCommentLikeDTO.getComment())
                .orElseThrow(() -> {
                    log.error("Failed to find comment with ID: {}", newCommentLikeDTO.getComment());
                    return new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID", newCommentLikeDTO.getComment());
                });
        CommentLike newCommentLike = new CommentLike();
        newCommentLike.setUser(user);
        newCommentLike.setComment(comment);
        newCommentLike.setReaction(newCommentLikeDTO.getReaction());
        CommentLike savedCommentLike = repository.save(newCommentLike);
        notificationService.setCommentLike(savedCommentLike);
        notificationService.sendNotification(NotificationType.COMMENT_LIKE);

        // Real-time notification
        notificationService.sendRealTimeNotification(NotificationDTO.fromNotification(
                Notification.builder()
                        .notificationType(NotificationType.COMMENT_LIKE)
                        .content(user.getUsername() + " liked your comment")
                        .user(comment.getUser())
                        .notifiedAt(LocalDateTime.now())
                        .build()
        ));

        log.info("New like added for comment ID: {}", newCommentLike.getComment().getId());

        // Create entity model and return response
        EntityModel<CommentLikeDTO> entityModel = assembler.toModel(savedCommentLike);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/{userId}")
    ResponseEntity<?> delete(@PathVariable Long commentId, @PathVariable Long userId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot delete like for someone else");
            throw new CustomExceptionHandling.InvalidArgumentException(
                    "You cannot delete like for someone else");
        }
        log.debug("Deleting like for comment ID: {} and user ID: {}", commentId, userId);
        repository.deleteById(new CommentLike.CommentLikeId(commentId, userId));
        log.info("Like deleted for comment ID: {} and user ID: {}", commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
