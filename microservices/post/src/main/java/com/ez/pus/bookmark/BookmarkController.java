package com.ez.pus.bookmark;

import com.ez.pus.dto.UserDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.post.Post;
import com.ez.pus.post.PostRepository;
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


import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookmarks")
public class BookmarkController {

    private static final Logger log = LoggerFactory.getLogger(BookmarkController.class);
    private final UserClient userClient;
    private final BookmarkRepository repository;
    private final PostRepository postRepository;
    private final BookmarkModelAssembler assembler;


    @GetMapping
    public CollectionModel<EntityModel<BookmarkDTO>> all(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "3") int size,
                                                         @RequestParam(defaultValue = "savedAt") String sort) {
        Long userId = userClient.getCurrentUser().getId();
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
        return bookmarks;
    }

    @GetMapping("/{postId}")
    public EntityModel<BookmarkDTO> one(@PathVariable Long postId) {
        Long userId = userClient.getCurrentUser().getId();
        log.trace("Request to get a single bookmark for userId: {} and postId: {}", userId, postId);
        Bookmark bookmark = repository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(() -> {
                    log.error("Bookmark not found for userId: {} and postId: {}", userId, postId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Bookmark", "id", postId);
                });
        log.info("Bookmark retrieved for post: {}, by user: {}", bookmark.getPost().getId(), bookmark.getUserId());
        return assembler.toModel(bookmark);
    }

    @PostMapping
    ResponseEntity<?> newBookmark(@Valid @RequestBody BookmarkDTO bookmark) {
        UserDTO user = userClient.getCurrentUser();
        Long userId = user.getId();
        log.debug("Request to create a new bookmark for userId: {}", userId);
        Post post = postRepository.findById(bookmark.getPost().getId())
                .orElseThrow(() -> {
                    log.error("Post not found for postId: {}", bookmark.getPost());
                    return new CustomExceptionHandling.ResourceNotFoundException("Post", "id",
                            bookmark.getPost());
                });
        Bookmark newBookmark = new Bookmark(userId, post, LocalDateTime.now());
        EntityModel<BookmarkDTO> entityModel = assembler.toModel(repository.save(newBookmark));
        log.info("New bookmark created: for post {} by user: {}", newBookmark.getPost().getId(), newBookmark.getUserId());
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/{postId}")
    ResponseEntity<?> deleteBookmark(@PathVariable Long postId) {
        Long userId = userClient.getCurrentUser().getId();
        log.info("Request to delete bookmark for userId: {} and postId: {}", userId, postId);
        repository.deleteById(new Bookmark.BookmarkId(userId, postId));
        log.info("Bookmark deleted for userId: {} and postId: {}", userId, postId);
        return ResponseEntity.noContent().build();
    }
}
