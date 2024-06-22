package com.ez.pus.controller;

import com.ez.pus.UploadFiles.storage.StorageService;
import com.ez.pus.dto.PostDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.repository.PostLikeRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ez.pus.model.Post;
import com.ez.pus.repository.FollowerRepository;
import com.ez.pus.repository.PostRepository;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(PostController.class);
    private final UserRepository userRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final PostModelAssembler assembler;
    private final FollowerRepository followerRepository;
    private final StorageService storageService;

    @Component
    public static class PostModelAssembler implements RepresentationModelAssembler<Post, EntityModel<PostDTO>> {
        @Override
        public @NonNull EntityModel<PostDTO> toModel(@NonNull Post post) {
            return EntityModel.of(PostDTO.fromPost(post),
                    linkTo(methodOn(PostController.class).one(post.getId())).withSelfRel(),
                    linkTo(methodOn(PostController.class).getAllPostsByUserId(post.getUser().getId(), 0, 3, "createdAt")).withRel("userPosts"),
                    linkTo(methodOn(PostController.class).all(0, 3, "createdAt")).withRel("allPosts"), linkTo(methodOn(PostController.class).getAllPostsByFollowing(0, 3, "createdAt")).withRel("PostsByFollowing"),
                    linkTo(methodOn(PostController.class).getAllRecentPostsByFollowing(0, 3, "createdAt")).withRel("recentPostsByFollowing"),
                    linkTo(methodOn(CommentController.class).all(post.getId(), 0, 3, "commentedAt")).withRel("comments"),
                    linkTo(methodOn(PostLikeController.class).getAllLikesForPost(post.getId(), 0, 3, "likedAt")).withRel("likes"));
        }
    }

    @GetMapping("/{postId}")
    public EntityModel<PostDTO> one(@PathVariable Long postId) {
        logger.trace("Fetching post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID", postId);
                });
        logger.debug("Post found with Id: {}", postId);
        return assembler.toModel(post);
    }

    @GetMapping("/user/{userId}")
    public CollectionModel<EntityModel<PostDTO>> getAllPostsByUserId(
            @PathVariable long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {

        logger.trace("Fetching all posts by user ID: {}", userId);
        List<Post> userPosts = postRepository.findByUserId(userId,
                PageRequest.of(page, size, Sort.by(sort).descending()));
        if (userPosts.isEmpty()) {
            return CollectionModel.of(List.of());
        }

        logger.debug("Posts found for user with ID {}", userId);
        List<EntityModel<PostDTO>> posts = userPosts.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        List<Post> nextPage = postRepository.findByUserId(userId, PageRequest.of(page + 1, size, Sort.by(sort)));
        return CollectionModel.of(posts)
                .addIf(page > 0,
                        () -> linkTo(methodOn(PostController.class).getAllPostsByUserId(userId, page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(PostController.class).getAllPostsByUserId(userId, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(PostController.class).getAllPostsByUserId(userId, page + 1, size, sort))
                                .withRel("Next Page"));
    }

    @GetMapping("/user/following")
    public CollectionModel<EntityModel<PostDTO>> getAllPostsByFollowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        logger.trace("Fetching all posts by following users of user ID: {}", userId);
        List<Long> followingIds = followerRepository.findAllByFollowerId(userId, PageRequest.of(0, 1000000)).stream()
                .map(follower -> follower.getFollowed().getId())
                .collect(Collectors.toList());

        if (followingIds.isEmpty()) {
            logger.warn("No following users found for user with ID: {}", userId);
            return CollectionModel.of(List.of());
        }

        List<Post> posts = postRepository.findAllByUserIdIn(followingIds, PageRequest.of(page, size));
        if (posts.isEmpty()) {
            logger.warn("No following users found for user with ID: {}", userId);
            return CollectionModel.of(List.of());
        }
        logger.debug("Posts found for following users of user with ID {}", userId);
        List<EntityModel<PostDTO>> postModels = posts.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        List<Post> nextPage = postRepository.findAllByUserIdIn(followingIds,
                PageRequest.of(page + 1, size, Sort.by(sort)));
        return CollectionModel.of(postModels)
                .addIf(page > 0, () -> linkTo(methodOn(PostController.class).getAllPostsByFollowing(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(PostController.class).getAllPostsByFollowing(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(), () -> linkTo(methodOn(PostController.class).getAllPostsByFollowing(page + 1, size, sort)).withRel("Next Page"));
    }

    @GetMapping("/user/following/recent")
    public CollectionModel<EntityModel<PostDTO>> getAllRecentPostsByFollowing(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        logger.trace("Fetching all recent posts by following users of user ID: {}", userId);
        List<Long> followingIds = followerRepository.findAllByFollowerId(userId, PageRequest.of(0, 1000000)).stream()
                .map(follower -> follower.getFollowed().getId())
                .collect(Collectors.toList());
        if (followingIds.isEmpty()) {
            logger.warn("No following users found for user with ID: {}", userId);
            return CollectionModel.of(List.of());
        }
        List<Post> posts = postRepository.findAllByUserIdIn(followingIds,
                PageRequest.of(page, size, Sort.by(sort).descending()));
        if (posts.isEmpty()) {
            logger.warn("No recent posts found for following users of user with ID: {}", userId);
            return CollectionModel.of(List.of());
        }
        logger.debug("Recent posts found for following users of user with ID {}", userId);
        List<EntityModel<PostDTO>> postModels = posts.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        List<Post> nextPage = postRepository.findAllByUserIdIn(followingIds, PageRequest.of(page + 1, size));
        return CollectionModel.of(postModels)
                .addIf(page > 0, () -> linkTo(methodOn(PostController.class).getAllRecentPostsByFollowing(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(PostController.class).getAllRecentPostsByFollowing(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(), () -> linkTo(methodOn(PostController.class).getAllPostsByFollowing(page + 1, size, sort)).withRel("Next Page"));
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

        if (posts.isEmpty()) {
            return CollectionModel.of(List.of());
        }

        List<Post> nextPage = postRepository.findAllPosts(PageRequest.of(page + 1, size, Sort.by(sort)));
        logger.debug("Posts found");

        return CollectionModel.of(posts)
                .addIf(page > 0,
                        () -> linkTo(methodOn(PostController.class).all(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(PostController.class).all(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(PostController.class).all(page + 1, size, sort)).withRel("Next Page"));
    }

     @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
        public ResponseEntity<EntityModel<PostDTO>> addPost(
                @RequestPart("post") String postJson,
                @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        PostDTO postDTO = objectMapper.readValue(postJson, PostDTO.class);

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
                imageUrl = storageService.store(file);
        }

        if ((postDTO.getCaption() == null || postDTO.getCaption().isEmpty()) && (file == null || file.isEmpty())) {
                throw new IllegalArgumentException("If there is no caption, there must be a file.");
        }

        Post post = Post.builder()
                .caption(postDTO.getCaption())
                .view(postDTO.getView())
                .imageUrl(imageUrl)
                .build();

        post.setUser(userRepository.findById(
                userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())
        ).get());

        logger.trace("Adding new post with image");

        Post newPost = postRepository.save(post);

        EntityModel<PostDTO> entityModel = assembler.toModel(newPost);

        logger.debug("New post with image added");

        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
        }


        @PutMapping("/{postId}")
        public ResponseEntity<EntityModel<PostDTO>> updatePost(@PathVariable Long postId, @RequestBody PostDTO newpost) {
                logger.trace("Updating post with ID: {}", postId);
                Post updatedPost = postRepository.findById(postId)
                        .map(post -> {
                        post.setCaption(newpost.getCaption());
                        post.setView(newpost.getView());
                        return postRepository.save(post);
                        })
                        .orElseThrow(() -> {
                        logger.error("Post not found with ID: {}", postId);
                        return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID", postId);
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

        List<Long> trendingPosts = postLikeRepository.findTrending(LocalDateTime.now().minusDays(days), PageRequest.of(page, size));
        logger.info("Getting trending posts successful");

        List<EntityModel<PostDTO>> posts = postRepository.findAllByIdIn(trendingPosts).stream()
                .map(assembler::toModel)
                .toList();

        if (posts.isEmpty()) {
            return CollectionModel.of(List.of());
        }

        List<Long> nextPage = postLikeRepository.findTrending(LocalDateTime.now().minusDays(days), PageRequest.of(page + 1, size));
        return CollectionModel.of(posts)
                .addIf(page > 0, () -> linkTo(methodOn(PostController.class).getTrending(days, page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(PostController.class).getTrending(days, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(), () -> linkTo(methodOn(PostController.class).getTrending(days, page + 1, size, sort)).withRel("Next Page"));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        logger.trace("Deleting post with ID: {}", postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    logger.error("Post not found with ID: {}", postId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Post", "ID", postId);
                });
        if (!Objects.equals(post.getUser().getUsername(), AuthTokenFilter.getCurrentUserByJwt())) {
            logger.error("You cannot delete post for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete post for another user");
        }
        postRepository.delete(post);
        logger.debug("Post deleted");
        return ResponseEntity.noContent().build();
    }
}
