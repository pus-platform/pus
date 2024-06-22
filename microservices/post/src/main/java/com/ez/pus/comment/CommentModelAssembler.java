package com.ez.pus.comment;

import com.ez.pus.commentLike.CommentLikeController;
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
class CommentModelAssembler implements RepresentationModelAssembler<Comment, EntityModel<CommentDTO>> {

    @NonNull
    @Override
    public EntityModel<CommentDTO> toModel(@NonNull Comment comment) {
        log.trace("Assembling model for comment: {}", comment.getId());
        return EntityModel.of(CommentDTO.fromComment(comment),
                linkTo(methodOn(CommentController.class).one(comment.getId())).withSelfRel(),
                linkTo(methodOn(PostController.class).one(comment.getPost().getId(), null)).withRel("post"),
                linkTo(methodOn(CommentLikeController.class).all(comment.getPost().getId(), comment.getId(), 0, 3,
                        "commentedAt")).withRel("likes"),
                linkTo(methodOn(CommentController.class).replies(comment.getPost().getId(), comment.getId(), 0, 3,
                        "commentedAt")).withRel("replies"));
    }
}