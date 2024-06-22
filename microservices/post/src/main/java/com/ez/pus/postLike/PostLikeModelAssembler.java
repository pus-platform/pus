package com.ez.pus.postLike;

import com.ez.pus.post.PostController;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@Component
class PostLikeModelAssembler implements RepresentationModelAssembler<PostLike, EntityModel<PostLikeDTO>> {

        @Override
        public @NonNull EntityModel<PostLikeDTO> toModel(@NonNull PostLike postLike) {
                log.debug("Assembling model for PostLike with Post ID: {} and User ID: {}",
                                postLike.getPost().getId(),
                                postLike.getUserId());
                return EntityModel.of(PostLikeDTO.fromPostLike(postLike),
                                linkTo(methodOn(PostLikeController.class).getLike(postLike.getPost().getId(),
                                                postLike.getUserId())).withSelfRel(),
                                linkTo(methodOn(PostLikeController.class).getAllLikesForPost(
                                                postLike.getPost().getId(), 0, 3,
                                                "likedAt")).withRel("postLikes"),
                                linkTo(methodOn(PostController.class).one(postLike.getPost().getId(), null))
                                                .withRel("post")
                );
        }
}
