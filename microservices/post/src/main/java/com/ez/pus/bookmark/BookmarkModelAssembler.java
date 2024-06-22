package com.ez.pus.bookmark;

import com.ez.pus.post.PostController;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import org.springframework.lang.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class BookmarkModelAssembler implements RepresentationModelAssembler<Bookmark, EntityModel<BookmarkDTO>> {

    private static final Logger log = LoggerFactory.getLogger(BookmarkModelAssembler.class);

    @NonNull
    @Override
    public EntityModel<BookmarkDTO> toModel(@NonNull Bookmark bookmark) {
        log.trace("Assembling Bookmark Model for Bookmark of post: {} and user: {}", bookmark.getPost().getId(), bookmark.getUserId());
        log.debug("Model assembly for user: {}", bookmark.getUserId());
        log.info("Bookmark model assembled with postId: {}", bookmark.getPost().getId());

        return EntityModel.of(BookmarkDTO.fromBookmark(bookmark),
                linkTo(methodOn(BookmarkController.class).one(bookmark.getPost().getId())).withSelfRel(),
                linkTo(methodOn(PostController.class).one(bookmark.getPost().getId(), null)).withRel("post"),
                linkTo(methodOn(BookmarkController.class).all(0, 3, "savedAt")).withRel("bookmarks"));
    }
}
