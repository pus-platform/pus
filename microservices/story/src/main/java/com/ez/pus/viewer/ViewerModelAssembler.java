package com.ez.pus.viewer;

import com.ez.pus.story.StoryController;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Log4j2
@Component
class ViewerModelAssembler implements RepresentationModelAssembler<Viewer, EntityModel<ViewerDTO>> {

    @Override
    public @NonNull EntityModel<ViewerDTO> toModel(Viewer viewer) {
        log.trace("Assembling model for viewer with user ID: {} and story ID: {}",
                viewer.getUserId(),
                viewer.getStory().getId());
        return EntityModel.of(ViewerDTO.fromViewer(viewer),
                linkTo(methodOn(ViewerController.class).getViews(viewer.getStory().getId(), 0, 3, "viewedAt"))
                        .withSelfRel(),
                linkTo(methodOn(StoryController.class).getStoryById(viewer.getStory().getId())).withRel("story"));
    }
}