package com.ez.pus.commentLike;

import com.ez.pus.comment.Comment;
import com.ez.pus.comment.CommentRepository;
import com.ez.pus.dto.UserDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UserClient;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/posts/{postId}/comments/{commentId}/likes")
public class CommentLikeController {

        private static final Logger log = LoggerFactory.getLogger(CommentLikeController.class);
        private final UserClient userClient;
        private final CommentLikeRepository repository;
        final CommentRepository commentRepository;
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
        public CollectionModel<EntityModel<CommentLikeDTO>> all(@PathVariable Long postId, @PathVariable Long commentId,
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
                return likes;
        }

        @PutMapping("/{userId}")
        ResponseEntity<?> edit(@Valid @RequestBody CommentLikeDTO likeDTO, @PathVariable Long commentId,
                        @PathVariable Long userId) {
                UserDTO user = userClient.getCurrentUser();
                if (!Objects.equals(userId, user.getId())) {
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
                                                        .userId(userId)
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
                newCommentLikeDTO.setUser(userClient.getCurrentUser().getId());
                UserDTO user = userClient.getCurrentUser();
                if (!Objects.equals(newCommentLikeDTO.getUser(), user.getId())) {
                        log.error("You cannot delete like for someone else");
                        throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID",
                                        newCommentLikeDTO.getUser());
                }

                Comment comment = commentRepository.findById(newCommentLikeDTO.getComment())
                                .orElseThrow(() -> {
                                        log.error("Failed to find comment with ID: {}", newCommentLikeDTO.getComment());
                                        return new CustomExceptionHandling.ResourceNotFoundException("Comment", "ID",
                                                        newCommentLikeDTO.getComment());
                                });
                CommentLike newCommentLike = new CommentLike();
                newCommentLike.setUserId(user.getId());
                newCommentLike.setComment(comment);
                newCommentLike.setReaction(newCommentLikeDTO.getReaction());
                CommentLike savedCommentLike = repository.save(newCommentLike);

                log.info("New like added for comment ID: {}", newCommentLike.getComment().getId());

                // Create entity model and return response
                EntityModel<CommentLikeDTO> entityModel = assembler.toModel(savedCommentLike);
                return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                                .body(entityModel);
        }

        @DeleteMapping("/{userId}")
        ResponseEntity<?> delete(@PathVariable Long commentId, @PathVariable Long userId) {
                if (!Objects.equals(userId, userClient.getCurrentUser().getId())) {
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
