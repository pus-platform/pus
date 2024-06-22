package com.ez.pus.controller;

import com.ez.pus.dto.CommentDTO;
import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.dto.ShortUserDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Comment;
import com.ez.pus.model.Notification;
import com.ez.pus.model.Post;
import com.ez.pus.model.User;
import com.ez.pus.repository.CommentRepository;
import com.ez.pus.repository.PostRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.service.NotificationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
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

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private static final Logger log = LoggerFactory.getLogger(CommentController.class);
    private final CommentRepository commentRepository;

    @Component
    static class CommentModelAssembler implements RepresentationModelAssembler<Comment, EntityModel<CommentDTO>> {

        @NonNull
        @Override
        public EntityModel<CommentDTO> toModel(@NonNull Comment comment) {
            log.trace("Assembling model for comment: {}", comment.getId());
            return EntityModel.of(CommentDTO.fromComment(comment),
                    linkTo(methodOn(CommentController.class).one(comment.getId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).one(comment.getUser().getId())).withRel("author"),
                    linkTo(methodOn(PostController.class).one(comment.getPost().getId())).withRel("post"),
                    linkTo(methodOn(CommentLikeController.class).all(comment.getPost().getId(), comment.getId(), 0, 3, "commentedAt")).withRel("likes"),
                    linkTo(methodOn(CommentController.class).replies(comment.getPost().getId(), comment.getId(), 0, 3, "commentedAt")).withRel("replies"));
        }
    }

    private final CommentRepository repository;
    private final CommentModelAssembler assembler;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final NotificationService notificationService;

    @GetMapping(value = {"/{commentId}", "/{repliedId}/replies/{commentId}"})
    public EntityModel<CommentDTO> one(@PathVariable("commentId") Long commentId) {
        log.debug("Fetching comment with ID: {}", commentId);
        Comment comment = repository.findById(commentId)
                .orElseThrow(() -> {
                    log.error("Comment not found with ID: {}", commentId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID", commentId);
                });
        log.info("Comment retrieved with ID: {}", commentId);
        return assembler.toModel(comment);
    }

    @GetMapping
    public CollectionModel<EntityModel<CommentDTO>> all(@PathVariable("postId") Long postId,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "3") int size,
                                                        @RequestParam(defaultValue = "commentedAt") String sort) {
        log.debug("Listing comments for post ID: {} with page: {}, size: {}, and sort: {}", postId, page, size, sort);
        List<Comment> nextPage = repository.findAllByPostId(postId, PageRequest.of(page + 1, size, Sort.by(sort)));
        CollectionModel<EntityModel<CommentDTO>> comments = CollectionModel.of(
                        repository.findAllByPostId(postId, PageRequest.of(page, size, Sort.by(sort).descending())).stream()
                                .map(assembler::toModel)
                                .toList())
                .addIf(page > 0,
                        () -> linkTo(methodOn(CommentController.class).all(postId, page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(CommentController.class).all(postId, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(CommentController.class).all(postId, page + 1, size, sort))
                                .withRel("Next Page"));
        log.info("Comments listed for post ID: {}", postId);
        if (comments.getContent().isEmpty()) {
            return CollectionModel.of(List.of());
        }
        return comments;
    }

    @PostMapping(value = {"", "/{repliedId}/replies"})
    public ResponseEntity<?> add(@Valid @RequestBody CommentDTO commentDTO) {
        commentDTO.setUser(ShortUserDTO.builder().id(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())).build());
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        log.debug("Adding new comment for user ID: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.error("User not found with ID: {}", commentDTO.getUser().getId());
            return new CustomExceptionHandling.ResourceNotFoundException("User", "ID", commentDTO.getUser().getId());
        });
        Post post = postRepository.findById(commentDTO.getPost()).orElseThrow(() -> {
            log.error("Post not found with ID: {}", commentDTO.getPost());
            return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID", commentDTO.getPost());
        });
        Comment comment = Comment.builder()
                .content(commentDTO.getContent())
                .build();
        comment.setUser(user);
        comment.setPost(post);
        if (commentDTO.getRepliedComment() != null) {
            Comment repliedComment = repository.findById(commentDTO.getRepliedComment()).orElseThrow(() -> {
                log.error("Replied comment not found with ID: {}", commentDTO.getRepliedComment());
                return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID", commentDTO.getRepliedComment());
            });
            comment.setRepliedComment(repliedComment);
        }
        Comment savedComment = repository.save(comment);
        EntityModel<CommentDTO> commentEntityModel = assembler.toModel(savedComment);
        notificationService.setComment(savedComment);
        notificationService.sendNotification(
                comment.getRepliedComment() == null ? NotificationType.COMMENT : NotificationType.COMMENT);
        /* to-do changed Notification type for replied comment */

        // Real-time notification
        notificationService.sendRealTimeNotification(NotificationDTO.fromNotification(
                Notification.builder()
                        .notificationType(comment.getRepliedComment() == null ? NotificationType.COMMENT : NotificationType.COMMENT_REPLY)
                        .content(user.getUsername() + (comment.getRepliedComment() == null ? " commented on your post" : " replied to your comment"))
                        .user(comment.getRepliedComment() == null ? post.getUser() : comment.getRepliedComment().getUser())
                        .notifiedAt(LocalDateTime.now())
                        .build()
        ));

        log.info("New comment added for post ID: {}", commentDTO.getPost());
        return ResponseEntity.created(commentEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(commentEntityModel);
    }

    @DeleteMapping(value = {"/{commentId}", "/{repliedId}/replies/{commentId}"})
    ResponseEntity<?> delete(@PathVariable("commentId") Long commentId) {
        log.info("Deleting comment with ID: {}", commentId);
        String username = commentRepository.findById(commentId).orElseThrow(
                        () -> new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID", commentId))
                .getUser().getUsername();
        if (!Objects.equals(username, AuthTokenFilter.getCurrentUserByJwt())) {
            log.error("You cannot delete comment for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete comment for another user");
        }
        repository.deleteById(commentId);
        log.info("Comment deleted with ID: {}", commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{repliedId}/replies")
    public CollectionModel<EntityModel<CommentDTO>> replies(@PathVariable Long postId,
                                                            @PathVariable("repliedId") Long replyId, @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "3") int size, @RequestParam(defaultValue = "commentedAt") String sort) {
        log.debug("Fetching replies for comment ID: {} with page: {}, size: {}, and sort: {}", replyId, page, size,
                sort);
        List<Comment> nextPage = repository.findAllByRepliedCommentId(postId,
                PageRequest.of(page + 1, size, Sort.by(sort)));
        CollectionModel<EntityModel<CommentDTO>> replies = CollectionModel.of(
                        repository.findAllByRepliedCommentId(postId, PageRequest.of(page, size, Sort.by(sort).descending()))
                                .stream()
                                .map(assembler::toModel)
                                .toList())
                .addIf(page > 0,
                        () -> linkTo(methodOn(CommentController.class).replies(postId, replyId, page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(CommentController.class).replies(postId, replyId, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(CommentController.class).replies(postId, replyId, page + 1, size, sort))
                                .withRel("Next Page"));
        log.info("Replies fetched for comment ID: {}", replyId);
        if (replies.getContent().isEmpty()) {
            return CollectionModel.of(List.of());
        }
        return replies;
    }

    @PutMapping(value = {"/{commentId}", "/{repliedId}/replies/{commentId}"})
    public ResponseEntity<?> edit(@Valid @RequestBody CommentDTO editedCommentDTO,
                                  @PathVariable("commentId") Long commentId) {
        log.debug("Editing comment with ID: {}", commentId);
        String username = commentRepository.findById(commentId).orElseThrow(
                        () -> new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID", commentId))
                .getUser().getUsername();
        if (!Objects.equals(username, AuthTokenFilter.getCurrentUserByJwt())) {
            log.error("You cannot edit comment for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot edit comment for another user");
        }
        Comment edited = repository.findById(commentId)
                .map(comment -> {
                    log.trace("Updating content for comment ID: {}", commentId);
                    comment.setContent(editedCommentDTO.getContent());
                    return repository.save(comment);
                })
                .orElseGet(() -> {
                    log.trace("Creating new comment for ID: {} due to not found", commentId);
                    Comment editedComment = Comment.builder()
                            .post(postRepository.findById(editedCommentDTO.getPost()).get())
                            .user(userRepository.findById(editedCommentDTO.getUser().getId()).get())
                            .content(editedCommentDTO.getContent())
                            .id(commentId).build();
                    return repository.save(editedComment);
                });
        EntityModel<CommentDTO> entityModel = assembler.toModel(edited);
        log.info("Comment edited with ID: {}", edited.getId());
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }
}
