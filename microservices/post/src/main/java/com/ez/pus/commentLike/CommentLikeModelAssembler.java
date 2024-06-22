package com.ez.pus.commentLike;

import com.ez.pus.comment.CommentController;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@Component
class CommentLikeModelAssembler
                implements RepresentationModelAssembler<CommentLike, EntityModel<CommentLikeDTO>> {

        @NonNull
        @Override
        public EntityModel<CommentLikeDTO> toModel(@NonNull CommentLike commentLike) {
                log.trace("Assembling model for comment like by user ID: {}", commentLike.getUserId());
                return EntityModel.of(CommentLikeDTO.fromCommentLike(commentLike),
                                linkTo(methodOn(CommentLikeController.class).one(
                                                commentLike.getComment().getPost().getId(),
                                                commentLike.getComment().getId(), commentLike.getUserId()))
                                                .withSelfRel(),
                                linkTo(methodOn(CommentLikeController.class).all(
                                                commentLike.getComment().getPost().getId(),
                                                commentLike.getComment().getId(), 0, 3, "likedAt"))
                                                .withRel("comment likes"),
                                linkTo(methodOn(CommentController.class).one(commentLike.getComment().getId()))
                                                .withRel("comment"));
        }
}
