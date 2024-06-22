package com.ez.pus.controller;

import com.ez.pus.dto.BookmarkDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Bookmark;
import com.ez.pus.model.Post;
import com.ez.pus.model.User;
import com.ez.pus.repository.BookmarkRepository;
import com.ez.pus.repository.PostRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import jakarta.validation.Valid;
import lombok.NonNull;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
public class BookmarkController {

    private static final Logger log = LoggerFactory.getLogger(BookmarkController.class);

    @Component
    static class BookmarkModelAssembler implements RepresentationModelAssembler<Bookmark, EntityModel<BookmarkDTO>> {

        @NonNull
        @Override
        public EntityModel<BookmarkDTO> toModel(@NonNull Bookmark bookmark) {
            log.trace("Assembling Bookmark Model for Bookmark of post: {} and user: {}", bookmark.getPost().getId(),
                    bookmark.getUser().getId());
            log.debug("Model assembly for user: {}", bookmark.getUser().getId());
            log.info("Bookmark model assembled with postId: {}", bookmark.getPost().getId());
            return EntityModel.of(BookmarkDTO.fromBookmark(bookmark),
                    linkTo(methodOn(BookmarkController.class).one(bookmark.getPost().getId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).one(bookmark.getUser().getId())).withRel("user"),
                    linkTo(methodOn(PostController.class).one(bookmark.getPost().getId())).withRel("post"),
                    linkTo(methodOn(BookmarkController.class).all(0, 3, "savedAt")).withRel("bookmarks"));
        }
    }

    private final BookmarkRepository repository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BookmarkModelAssembler assembler;

    BookmarkController(BookmarkRepository repository, BookmarkModelAssembler assembler, UserRepository userRepository,
                       PostRepository postRepository) {
        this.repository = repository;
        this.assembler = assembler;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        log.info("BookmarkController initialized");
    }

    @GetMapping("/bookmarks")
    public CollectionModel<EntityModel<BookmarkDTO>> all(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "3") int size,
                                                         @RequestParam(defaultValue = "savedAt") String sort) {
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        log.trace("Request to get all bookmarks for userId: {}, page: {}, size: {}, sort: {}", userId, page, size, sort);
        List<Bookmark> nextPage = repository.findAllByUserId(userId, PageRequest.of(page + 1, size, Sort.by(sort)));
        log.debug("Bookmarks retrieved for user: {}", userId);
        CollectionModel<EntityModel<BookmarkDTO>> bookmarks = CollectionModel.of(
                        repository.findAllByUserId(userId, PageRequest.of(page, size, Sort.by(sort).descending())).stream()
                                .map(assembler::toModel)
                                .toList())
                .addIf(page > 0,
                        () -> linkTo(methodOn(BookmarkController.class).all(page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(BookmarkController.class).all(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(BookmarkController.class).all(page + 1, size, sort))
                                .withRel("Next Page"));
        log.info("Bookmarks retrieved for user: {}", userId);

        if (bookmarks.getContent().isEmpty()) {
            return CollectionModel.of(List.of());
        }

        return bookmarks;
    }

    @GetMapping("/bookmarks/{postId}")
    public EntityModel<BookmarkDTO> one(@PathVariable Long postId) {
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        log.trace("Request to get a single bookmark for userId: {} and postId: {}", userId, postId);
        Bookmark bookmark = repository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> {
                    log.error("Bookmark not found for userId: {} and postId: {}", userId, postId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Bookmark", "id", postId);
                });
        log.info("Bookmark retrieved for post: {}, by user: {}", bookmark.getPost().getId(), bookmark.getUser().getId());
        return assembler.toModel(bookmark);
    }

    @PostMapping("/bookmarks")
    ResponseEntity<?> newBookmark(@Valid @RequestBody BookmarkDTO bookmark) {
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        log.debug("Request to create a new bookmark for userId: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found for userId: {}", userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "id", userId);
                });
        Post post = postRepository.findById(bookmark.getPost().getId())
                .orElseThrow(() -> {
                    log.error("Post not found for postId: {}", bookmark.getPost());
                    return new CustomExceptionHandling.ResourceNotFoundException("Post", "id",
                            bookmark.getPost());
                });
        Bookmark newBookmark = new Bookmark(user, post, LocalDateTime.now());
        EntityModel<BookmarkDTO> entityModel = assembler.toModel(repository.save(newBookmark));
        log.info("New bookmark created: for post {} by user: {}", newBookmark.getPost().getId(), newBookmark.getUser().getId());
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/bookmarks/{postId}")
    ResponseEntity<?> deleteBookmark(@PathVariable Long postId) {
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        log.info("Request to delete bookmark for userId: {} and postId: {}", userId, postId);
        repository.deleteById(new Bookmark.BookmarkId(userId, postId));
        log.info("Bookmark deleted for userId: {} and postId: {}", userId, postId);
        return ResponseEntity.noContent().build();
    }
}
