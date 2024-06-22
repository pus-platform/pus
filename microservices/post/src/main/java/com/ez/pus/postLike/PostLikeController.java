package com.ez.pus.postLike;

import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.post.PostRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts/{postId}/likes")
public class PostLikeController {
    private static final Logger logger = LoggerFactory.getLogger(PostLikeController.class);
    private final PostRepository postRepository;
    private final UserClient userClient;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeModelAssembler postLikeModelAssembler;

    @GetMapping("/{userId}")
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
        return postLikeModelAssembler.toModel(postLike);
    }

    @GetMapping
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
                .map(postLikeModelAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(likes);
    }

    @PostMapping
    public ResponseEntity<EntityModel<PostLikeDTO>> addLike(@Valid @RequestBody PostLikeDTO postLikeDTO) {
        logger.trace("Adding like for post ID: {}", postLikeDTO.getPost());
        PostLike postLike = PostLike.builder()
                .reaction(postLikeDTO.getReaction())
                .post(postRepository.findById(postLikeDTO.getPost().getId()).orElseThrow(() ->
                        new RuntimeException("Post not found")))
                .likedAt(postLikeDTO.getLikedAt())
                .build();
        postLike.setUserId(userClient.getCurrentUser().getId());
        PostLike newPostLike = postLikeRepository.save(postLike);
        logger.info("Like added for post ID: {}", postLike.getPost().getId());

        return ResponseEntity
                .created(linkTo(methodOn(PostLikeController.class).getLike(
                        newPostLike.getPost().getId(),
                        newPostLike.getUserId())).withSelfRel().toUri())
                .body(postLikeModelAssembler.toModel(newPostLike));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> edit(@Valid @RequestBody PostLikeDTO likeDTO, @PathVariable Long postId, @PathVariable Long userId) {
        if (!Objects.equals(userId, userClient.getCurrentUser().getId())) {
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
                        .userId(userId)
                        .build()
                ));
        logger.info("Like edited for post ID: {} and user ID: {}", postId, userId);
        return ResponseEntity
                .created(linkTo(
                        methodOn(PostLikeController.class).getLike(edited.getPost().getId(),
                                edited.getUserId()))
                        .withSelfRel().toUri())
                .body(postLikeModelAssembler.toModel(edited));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long postId, @PathVariable Long userId) {
        if (!Objects.equals(userId, userClient.getCurrentUser().getId())) {
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
