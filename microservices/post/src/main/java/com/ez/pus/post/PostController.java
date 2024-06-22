package com.ez.pus.post;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ez.pus.comment.CommentController;
import com.ez.pus.dto.UserDTO;
import com.ez.pus.enums.View;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UploadClient;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.postLike.PostLikeController;
import com.ez.pus.postLike.PostLikeRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@RefreshScope
public class PostController {

        private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PostController.class);
        private final PostLikeRepository postLikeRepository;

        @Component
        public static class PostModelAssembler implements RepresentationModelAssembler<Post, EntityModel<PostDTO>> {
                @Override
                public @NonNull EntityModel<PostDTO> toModel(@NonNull Post post) {
                        return EntityModel.of(PostDTO.fromPost(post),
                                        linkTo(methodOn(PostController.class).one(post.getId(), null)).withSelfRel(),
                                        linkTo(methodOn(PostController.class).getAllPostsByUserId(0, 3, "createdAt"))
                                                        .withRel("userPosts"),
                                        linkTo(methodOn(PostController.class).all(0, 3, "createdAt"))
                                                        .withRel("allPosts"),
                                        linkTo(methodOn(PostController.class).getAllPostsByFollowing(0, 3, "createdAt"))
                                                        .withRel("PostsByFollowing"),
                                        linkTo(methodOn(PostController.class).getAllRecentPostsByFollowing(0, 3,
                                                        "createdAt")).withRel("recentPostsByFollowing"),
                                        linkTo(methodOn(CommentController.class).all(post.getId(), 0, 3, "commentedAt"))
                                                        .withRel("comments"),
                                        linkTo(methodOn(PostLikeController.class).getAllLikesForPost(post.getId(), 0, 3,
                                                        "likedAt")).withRel("likes"));
                }
        }

        private final PostRepository postRepository;
        private final PostModelAssembler assembler;
        private final UploadClient uploadClient;
        private final UserClient userClient;

        @Value("${message: Hello Default}")
        String message;

        @RequestMapping("/message")
        public String getMessage() {
                return message;
        }

        @GetMapping("/{postId}")
        public EntityModel<PostDTO> one(@PathVariable Long postId, @RequestHeader("Authorization") String token) {
                if (token == null || token.isEmpty()) {
                        logger.error("No token provided");
                        throw new CustomExceptionHandling.InvalidArgumentException("No token provided");
                }
                logger.trace("Fetching post with ID: {}", postId);
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> {
                                        logger.error("Post not found with ID: {}", postId);
                                        return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                                                        postId);
                                });
                logger.debug("Post found with Id: {}", postId);
                return assembler.toModel(post);
        }

        @GetMapping("/user")
        public CollectionModel<EntityModel<PostDTO>> getAllPostsByUserId(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "3") int size,
                        @RequestParam(defaultValue = "createdAt") String sort) {

                UserDTO userDTO = userClient.getCurrentUser();
                System.out.println(userDTO);
                Long userId = userDTO.getId();
                logger.trace("Fetching all posts by user ID: {}", userId);
                List<Post> userPosts = postRepository.findByUser(userId,
                                PageRequest.of(page, size, Sort.by(sort).descending()));
                if (userPosts.isEmpty()) {
                        logger.warn("No posts found for user with ID: {}", userId);
                        CollectionModel.of(List.of());
                }

                logger.debug("Posts found for user with ID {}", userId);
                List<EntityModel<PostDTO>> posts = userPosts.stream()
                                .map(assembler::toModel)
                                .collect(Collectors.toList());
                List<Post> nextPage = postRepository.findByUser(userId,
                                PageRequest.of(page + 1, size, Sort.by(sort)));
                return CollectionModel.of(posts)
                                .addIf(page > 0,
                                                () -> linkTo(methodOn(PostController.class)
                                                                .getAllPostsByUserId(page - 1, size, sort))
                                                                .withRel("Previous Page"))
                                .add(linkTo(methodOn(PostController.class).getAllPostsByUserId(page, size, sort))
                                                .withSelfRel())
                                .addIf(!nextPage.isEmpty(),
                                                () -> linkTo(methodOn(PostController.class)
                                                                .getAllPostsByUserId(page + 1, size, sort))
                                                                .withRel("Next Page"));
        }

        @GetMapping("/user/following")
        public CollectionModel<EntityModel<PostDTO>> getAllPostsByFollowing(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "5") int size,
                        @RequestParam(defaultValue = "createdAt") String sort) {
                Long userId = userClient.getCurrentUser().getId();
                logger.trace("Fetching all posts by following users of user ID: {}", userId);
                List<Long> followingIds = userClient.allFollowing(userId, 0, 100000, "followedAt").getContent().stream()
                                .map(follower -> follower.getContent().getFollowed().getId())
                                .collect(Collectors.toList());

                if (followingIds.isEmpty()) {
                        logger.warn("No following users found for user with ID: {}", userId);
                        throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
                }
                List<Post> posts = postRepository.findAllByUserIn(followingIds, PageRequest.of(page, size));
                if (posts.isEmpty()) {
                        logger.warn("No posts found for following users of user with ID: {}", userId);
                        throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
                }
                logger.debug("Posts found for following users of user with ID {}", userId);
                List<EntityModel<PostDTO>> postModels = posts.stream()
                                .map(assembler::toModel)
                                .collect(Collectors.toList());
                List<Post> nextPage = postRepository.findAllByUserIn(followingIds,
                                PageRequest.of(page + 1, size, Sort.by(sort)));
                return CollectionModel.of(postModels)
                                .addIf(page > 0, () -> linkTo(methodOn(PostController.class)
                                                .getAllPostsByFollowing(page - 1, size, sort)).withRel("Previous Page"))
                                .add(linkTo(methodOn(PostController.class).getAllPostsByFollowing(page, size, sort))
                                                .withSelfRel())
                                .addIf(!nextPage.isEmpty(),
                                                () -> linkTo(methodOn(PostController.class)
                                                                .getAllPostsByFollowing(page + 1, size, sort))
                                                                .withRel("Next Page"));
        }

        @GetMapping("/user/following/recent")
        public CollectionModel<EntityModel<PostDTO>> getAllRecentPostsByFollowing(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "3") int size,
                        @RequestParam(defaultValue = "createdAt") String sort) {
                Long userId = userClient.getCurrentUser().getId();
                logger.trace("Fetching all recent posts by following users of user ID: {}", userId);
                List<Long> followingIds = userClient.allFollowing(userId, 0, 100000, "followedAt").getContent().stream()
                                .map(follower -> follower.getContent().getFollowed().getId())
                                .collect(Collectors.toList());
                if (followingIds.isEmpty()) {
                        logger.warn("No following users found for user with ID: {}", userId);
                        throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
                }
                List<Post> posts = postRepository.findAllByUserIn(followingIds,
                                PageRequest.of(page, size, Sort.by(sort).descending()));
                if (posts.isEmpty()) {
                        logger.warn("No recent posts found for following users of user with ID: {}", userId);
                        throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
                }
                logger.debug("Recent posts found for following users of user with ID {}", userId);
                List<EntityModel<PostDTO>> postModels = posts.stream()
                                .map(assembler::toModel)
                                .collect(Collectors.toList());
                List<Post> nextPage = postRepository.findAllByUserIn(followingIds, PageRequest.of(page + 1, size));
                return CollectionModel.of(postModels)
                                .addIf(page > 0, () -> linkTo(methodOn(PostController.class)
                                                .getAllRecentPostsByFollowing(page - 1, size, sort))
                                                .withRel("Previous Page"))
                                .add(linkTo(methodOn(PostController.class).getAllRecentPostsByFollowing(page, size,
                                                sort)).withSelfRel())
                                .addIf(!nextPage.isEmpty(),
                                                () -> linkTo(methodOn(PostController.class)
                                                                .getAllPostsByFollowing(page + 1, size, sort))
                                                                .withRel("Next Page"));
        }

        @GetMapping
        public CollectionModel<EntityModel<PostDTO>> all(@RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "10") int size,
                        @RequestParam(defaultValue = "createdAt") String sort) {
                logger.trace("Fetching all posts");
                List<EntityModel<PostDTO>> posts = postRepository
                                .findAllPosts(PageRequest.of(page, size, Sort.by(sort).descending())).stream()
                                .map(assembler::toModel)
                                .collect(Collectors.toList());
                List<Post> nextPage = postRepository.findAllPosts(PageRequest.of(page + 1, size, Sort.by(sort)));
                logger.debug("Posts found");
                return CollectionModel.of(posts)
                                .addIf(page > 0,
                                                () -> linkTo(methodOn(PostController.class).all(page - 1, size, sort))
                                                                .withRel("Previous Page"))
                                .add(linkTo(methodOn(PostController.class).all(page, size, sort)).withSelfRel())
                                .addIf(!nextPage.isEmpty(),
                                                () -> linkTo(methodOn(PostController.class).all(page + 1, size, sort))
                                                                .withRel("Next Page"));
        }

        @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        public ResponseEntity<EntityModel<PostDTO>> addPost(
                        @RequestPart("post") String postJson,
                        @RequestPart(value = "file", required = false) MultipartFile file)
                        throws JsonProcessingException {

                ObjectMapper objectMapper = new ObjectMapper();
                PostDTO postDTO = objectMapper.readValue(postJson, PostDTO.class);

                String imageUrl = null;
                if (file != null && !file.isEmpty()) {
                        imageUrl = uploadClient.handleFileUpload(file);
                }

                if ((postDTO.getCaption() == null || postDTO.getCaption().isEmpty())
                                && (file == null || file.isEmpty())) {
                        throw new IllegalArgumentException("If there is no caption, there must be a file.");
                }

                Post post = Post.builder()
                                .caption(postDTO.getCaption())
                                .location(postDTO.getLocation())
                                .view(postDTO.getView())
                                .imageUrl(imageUrl)
                                .build();

                post.setUser(userClient.getCurrentUser().getId());

                logger.trace("Adding new post with image");

                Post newPost = postRepository.save(post);

                EntityModel<PostDTO> entityModel = assembler.toModel(newPost);

                logger.debug("New post with image added");

                return ResponseEntity
                                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                                .body(entityModel);
        }

        @PutMapping("/{postId}")
        public ResponseEntity<EntityModel<PostDTO>> updatePost(@PathVariable Long postId,
                        @RequestBody PostDTO newpost) {
                logger.trace("Updating post with ID: {}", postId);
                Post updatedPost = postRepository.findById(postId)
                                .map(post -> {
                                        post.setLocation(newpost.getLocation());
                                        post.setCaption(newpost.getCaption());
                                        // TODO
                                        post.setView(View.PUBLIC);
                                        return postRepository.save(post);
                                })
                                .orElseThrow(() -> {
                                        logger.error("Post not found with ID: {}", postId);
                                        return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                                                        postId);
                                });
                EntityModel<PostDTO> entityModel = new PostModelAssembler().toModel(updatedPost);
                logger.debug("Post updated");
                return ResponseEntity
                                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                                .body(entityModel);
        }

        @GetMapping("/trending")
        public CollectionModel<EntityModel<PostDTO>> getTrending(@RequestParam(defaultValue = "7") long days,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "3") int size,
                        @RequestParam(defaultValue = "createdAt") String sort) {
                logger.info("Getting trending posts");

                List<Long> trendingPosts = postLikeRepository.findTrending(LocalDateTime.now().minusDays(days),
                                PageRequest.of(page, size));
                logger.info("Getting trending posts successful");

                List<EntityModel<PostDTO>> posts = postRepository.findAllByIdIn(trendingPosts).stream()
                                .map(assembler::toModel)
                                .toList();
                List<Long> nextPage = postLikeRepository.findTrending(LocalDateTime.now().minusDays(days),
                                PageRequest.of(page + 1, size));
                return CollectionModel.of(posts)
                                .addIf(page > 0, () -> linkTo(
                                                methodOn(PostController.class).getTrending(days, page - 1, size, sort))
                                                .withRel("Previous Page"))
                                .add(linkTo(methodOn(PostController.class).getTrending(days, page, size, sort))
                                                .withSelfRel())
                                .addIf(!nextPage.isEmpty(), () -> linkTo(
                                                methodOn(PostController.class).getTrending(days, page + 1, size, sort))
                                                .withRel("Next Page"));
        }

        @DeleteMapping("/{postId}")
        public ResponseEntity<?> deletePost(@PathVariable Long postId) {
                logger.trace("Deleting post with ID: {}", postId);
                Post post = postRepository.findById(postId)
                                .orElseThrow(() -> {
                                        logger.error("Post not found with ID: {}", postId);
                                        return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID",
                                                        postId);
                                });
                if (!Objects.equals(userClient.getUserById(post.getUser()).getUsername(),
                                userClient.getCurrentUser().getUsername())) {
                        logger.error("You cannot delete post for another user");
                        throw new CustomExceptionHandling.InvalidArgumentException(
                                        "You cannot delete post for another user");
                }
                postRepository.delete(post);
                logger.debug("Post deleted");
                return ResponseEntity.noContent().build();
        }
}