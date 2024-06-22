package com.ez.pus.viewer;

import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.story.StoryRepository;
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


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
public class ViewerController {

    private static final Logger logger = LoggerFactory.getLogger(ViewerController.class);
    private final UserClient userClient;

    private final ViewerRepository viewerRepository;

    private final StoryRepository storyRepository;

    private final ViewerModelAssembler assembler;

    @PostMapping("/{storyId}/views")
    public ResponseEntity<EntityModel<ViewerDTO>> addView(@PathVariable Long storyId,
                                                          @Valid @RequestBody ViewerDTO viewer) {
        logger.trace("Attempting to add a view for story with ID: {}", storyId);
        logger.debug("Adding view for story with ID: {}", storyId);
        if (!storyRepository.existsById(storyId)) {
            logger.warn("Story with ID: {} not found", storyId);
            logger.error("Failed to add view because story with ID: {} does not exist", storyId);
            throw new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
        }
        Viewer newViewer = viewerRepository.save(Viewer.builder()
                .userId(userClient.getCurrentUser().getId())
                .story(storyRepository.findById(viewer.getStory()).get())
                .viewedAt(viewer.getViewedAt())
                .build());
        EntityModel<ViewerDTO> entityModel = assembler.toModel(newViewer);
        logger.info("View added for story with ID: {}", storyId);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @GetMapping("/{storyId}/views")
    public CollectionModel<EntityModel<ViewerDTO>> getViews(@PathVariable Long storyId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "3") int size,
                                                            @RequestParam(defaultValue = "viewedAt") String sort) {
        logger.trace("Fetching views for story with ID: {}", storyId);
        logger.debug("Request to fetch views for story ID: {} with pagination settings page: {}, size: {}, sort: {}",
                storyId, page, size, sort);
        if (!storyRepository.existsById(storyId)) {
            logger.warn("Story with ID: {} not found", storyId);
            logger.error("Cannot fetch views because story with ID: {} does not exist", storyId);
            throw new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
        }
        if (!Objects.equals(storyRepository.findById(storyId).get().getUserId(), userClient.getCurrentUser().getId())) {
            logger.error("You cannot see views of another user's story");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot see views of another user's story");
        }
        List<Viewer> views = viewerRepository.findViewersByStoryId(storyId,
                PageRequest.of(page, size, Sort.by(sort).descending()));
        if (views.isEmpty()) {
            logger.warn("No views found for story with ID: {}", storyId);
            logger.error("Views retrieval failed for story with ID: {} due to no views existing", storyId);
            throw new CustomExceptionHandling.ResourceNotFoundException("Viewer", "storyId", storyId);
        }
        List<EntityModel<ViewerDTO>> list = views.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        List<Viewer> nextPage = viewerRepository.findViewersByStoryId(storyId,
                PageRequest.of(page + 1, size, Sort.by(sort)));
        logger.info("Successfully fetched views for story with ID: {}", storyId);
        return CollectionModel.of(list)
                .addIf(page > 0,
                        () -> linkTo(methodOn(ViewerController.class).getViews(storyId,
                                page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(ViewerController.class).getViews(storyId, page, size, sort))
                        .withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(ViewerController.class).getViews(storyId,
                                page + 1, size, sort))
                                .withRel("Next Page"));
    }
}
