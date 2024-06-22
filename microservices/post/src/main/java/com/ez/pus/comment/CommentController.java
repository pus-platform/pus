package com.ez.pus.comment;

import com.ez.pus.dto.UserDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.post.Post;
import com.ez.pus.post.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/comments")
public class CommentController {

        private static final Logger log = LoggerFactory.getLogger(CommentController.class);
        private final CommentRepository commentRepository;
        private final UserClient userClient;
        private final CommentRepository repository;
        private final CommentModelAssembler assembler;
        final PostRepository postRepository;

        @GetMapping(value = { "/{commentId}", "/{repliedId}/replies/{commentId}" })
        public EntityModel<CommentDTO> one(@PathVariable("commentId") Long commentId) {
                log.debug("Fetching comment with ID: {}", commentId);
                Comment comment = repository.findById(commentId)
                                .orElseThrow(() -> {
                                        log.error("Comment not found with ID: {}", commentId);
                                        return new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID",
                                                        commentId);
                                });
                log.info("Comment retrieved with ID: {}", commentId);
                return assembler.toModel(comment);
        }

        @GetMapping
        public CollectionModel<EntityModel<CommentDTO>> all(@PathVariable("postId") Long postId,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "3") int size,
                        @RequestParam(defaultValue = "commentedAt") String sort) {
                log.debug("Listing comments for post ID: {} with page: {}, size: {}, and sort: {}", postId, page, size,
                                sort);
                List<Comment> nextPage = repository.findAllByPostId(postId,
                                PageRequest.of(page + 1, size, Sort.by(sort)));
                CollectionModel<EntityModel<CommentDTO>> comments = CollectionModel.of(
                                repository.findAllByPostId(postId,
                                                PageRequest.of(page, size, Sort.by(sort).descending())).stream()
                                                .map(assembler::toModel)
                                                .toList())
                                .addIf(page > 0,
                                                () -> linkTo(methodOn(CommentController.class).all(postId, page - 1,
                                                                size, sort))
                                                                .withRel("Previous Page"))
                                .add(linkTo(methodOn(CommentController.class).all(postId, page, size, sort))
                                                .withSelfRel())
                                .addIf(!nextPage.isEmpty(),
                                                () -> linkTo(methodOn(CommentController.class).all(postId, page + 1,
                                                                size, sort))
                                                                .withRel("Next Page"));
                log.info("Comments listed for post ID: {}", postId);
                return comments;
        }


        @PostMapping(value = { "", "/{repliedId}/replies" })
        public ResponseEntity<?> add(@Valid @RequestBody CommentDTO commentDTO) {
                UserDTO user = userClient.getCurrentUser();
                commentDTO.setUser(user.getId());
                log.debug("Adding new comment for user ID: {}", commentDTO.getUser());
                Post post = postRepository.findById(commentDTO.getPost()).orElseThrow(() -> {
                        log.error("Post not found with ID: {}", commentDTO.getPost());
                        return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                                        commentDTO.getPost());
                });
                Comment comment = Comment.builder()
                                .content(commentDTO.getContent())
                                .build();
                comment.setUser(user.getId());
                comment.setPost(post);
                if (commentDTO.getRepliedComment() != null) {
                        Comment repliedComment = repository.findById(commentDTO.getRepliedComment()).orElseThrow(() -> {
                                log.error("Replied comment not found with ID: {}", commentDTO.getRepliedComment());
                                return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                                                commentDTO.getRepliedComment());
                        });
                        comment.setRepliedComment(repliedComment);
                }
                Comment savedComment = repository.save(comment);
                EntityModel<CommentDTO> commentEntityModel = assembler.toModel(savedComment);

                log.info("New comment added for post ID: {}", commentDTO.getPost());
                return ResponseEntity.created(commentEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                                .body(commentEntityModel);
        }

        @DeleteMapping(value = { "/{commentId}", "/{repliedId}/replies/{commentId}" })
        ResponseEntity<?> delete(@PathVariable("commentId") Long commentId) {
                log.info("Deleting comment with ID: {}", commentId);
                Long userId = commentRepository.findById(commentId).orElseThrow(
                                () -> new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID", commentId))
                                .getUser();
                String username = userClient.getUserById(userId).getUsername();
                if (!Objects.equals(username, userClient.getCurrentUser().getUsername())) {
                        log.error("You cannot delete comment for another user");
                        throw new CustomExceptionHandling.InvalidArgumentException(
                                        "You cannot delete comment for another user");
                }
                repository.deleteById(commentId);
                log.info("Comment deleted with ID: {}", commentId);
                return ResponseEntity.noContent().build();
        }

        @GetMapping("/{repliedId}/replies")
        public CollectionModel<EntityModel<CommentDTO>> replies(@PathVariable Long postId,
                        @PathVariable("repliedId") Long replyId, @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "3") int size,
                        @RequestParam(defaultValue = "commentedAt") String sort) {
                log.debug("Fetching replies for comment ID: {} with page: {}, size: {}, and sort: {}", replyId, page,
                                size,
                                sort);
                List<Comment> nextPage = repository.findAllByRepliedCommentId(postId,
                                PageRequest.of(page + 1, size, Sort.by(sort)));
                CollectionModel<EntityModel<CommentDTO>> replies = CollectionModel.of(
                                repository.findAllByRepliedCommentId(postId,
                                                PageRequest.of(page, size, Sort.by(sort).descending()))
                                                .stream()
                                                .map(assembler::toModel)
                                                .toList())
                                .addIf(page > 0,
                                                () -> linkTo(methodOn(CommentController.class).replies(postId, replyId,
                                                                page - 1, size, sort))
                                                                .withRel("Previous Page"))
                                .add(linkTo(methodOn(CommentController.class).replies(postId, replyId, page, size,
                                                sort)).withSelfRel())
                                .addIf(!nextPage.isEmpty(),
                                                () -> linkTo(methodOn(CommentController.class).replies(postId, replyId,
                                                                page + 1, size, sort))
                                                                .withRel("Next Page"));
                log.info("Replies fetched for comment ID: {}", replyId);
                return replies;
        }

        @PutMapping(value = { "/{commentId}", "/{repliedId}/replies/{commentId}" })
        public ResponseEntity<?> edit(@Valid @RequestBody CommentDTO editedCommentDTO,
                        @PathVariable("commentId") Long commentId) {
                log.debug("Editing comment with ID: {}", commentId);
                Long userId = commentRepository.findById(commentId).orElseThrow(
                                () -> new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID", commentId))
                                .getUser();
                String username = userClient.getUserById(userId).getUsername();
                if (!Objects.equals(username, userClient.getCurrentUser().getUsername())) {
                        log.error("You cannot edit comment for another user");
                        throw new CustomExceptionHandling.InvalidArgumentException(
                                        "You cannot edit comment for another user");
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
                                                        .user(userClient.getCurrentUser().getId())
                                                        .content(editedCommentDTO.getContent())
                                                        .id(commentId).build();
                                        return repository.save(editedComment);
                                });
                EntityModel<CommentDTO> entityModel = assembler.toModel(edited);
                log.info("Comment edited with ID: {}", edited.getId());
                return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                                .body(entityModel);
        }
}
