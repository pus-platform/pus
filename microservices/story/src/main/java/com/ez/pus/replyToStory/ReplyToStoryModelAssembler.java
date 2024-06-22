package com.ez.pus.replyToStory;

import com.ez.pus.story.StoryController;
import lombok.NonNull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
class ReplyToStoryModelAssembler
                implements RepresentationModelAssembler<ReplyToStory, EntityModel<ReplyToStoryDTO>> {
        @Override
        public @NonNull EntityModel<ReplyToStoryDTO> toModel(@NonNull ReplyToStory reply) {
                return EntityModel.of(ReplyToStoryDTO.fromReplyToStory(reply),
                                linkTo(methodOn(ReplyToStoryController.class).getReply(reply.getId())).withSelfRel(),
                                linkTo(methodOn(ReplyToStoryController.class)
                                                .getRepliesToStory(reply.getStory().getId()))
                                                .withRel("storyReplies"),
                                linkTo(methodOn(StoryController.class).getStoryById(reply.getStory().getId()))
                                                .withRel("story")
                );
        }
}
