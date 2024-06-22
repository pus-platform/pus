package com.ez.pus.controller;

import com.ez.pus.dto.ViewerDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Viewer;
import com.ez.pus.repository.StoryRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.repository.ViewerRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class ViewerController {

    private static final Logger logger = LoggerFactory.getLogger(ViewerController.class);
    private final UserRepository userRepository;
    private final ViewerRepository viewerRepository;
    private final StoryRepository storyRepository;
    private final ViewerModelAssembler assembler;

    @Component
    static class ViewerModelAssembler implements RepresentationModelAssembler<Viewer, EntityModel<ViewerDTO>> {
        @Override
        public @NonNull EntityModel<ViewerDTO> toModel(Viewer viewer) {
            logger.trace("Assembling model for viewer with user ID: {} and story ID: {}",
                    viewer.getUser().getId(),
                    viewer.getStory().getId());
            return EntityModel.of(ViewerDTO.fromViewer(viewer),
                    linkTo(methodOn(ViewerController.class).getViews(viewer.getStory().getId(), 0, 3, "viewedAt")).withSelfRel(),
                    linkTo(methodOn(StoryController.class).getStoryById(viewer.getStory().getId())).withRel("story"),
                    linkTo(methodOn(UserController.class).one(viewer.getUser().getId())).withRel("user"));
        }
    }

    @PostMapping("/{storyId}/views")
    public ResponseEntity<EntityModel<ViewerDTO>> addView(@PathVariable Long storyId,
                                                          @Valid @RequestBody ViewerDTO viewer) {
        logger.trace("Attempting to add a view for story with ID: {}", storyId);
        logger.debug("Adding view for story with ID: {}", storyId);
        System.out.println(viewer);
        if (!storyRepository.existsById(storyId)) {
            logger.warn("Story with ID: {} not found", storyId);
            logger.error("Failed to add view because story with ID: {} does not exist", storyId);
            throw new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
        }
        Viewer toSave = Viewer.builder()
                .user(userRepository.findById(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())).get())
                .story(storyRepository.findById(viewer.getStory()).get())
                .viewedAt(viewer.getViewedAt())
                .build();
        System.out.println(toSave);
        Viewer newViewer = viewerRepository.save(toSave);
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
        if (!Objects.equals(storyRepository.findById(storyId).get().getUser().getUsername(), AuthTokenFilter.getCurrentUserByJwt())) {
            logger.error("You cannot see views of another user's story");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot see views of another user's story");
        }
        List<Viewer> views = viewerRepository.findViewersByStoryId(storyId,
                PageRequest.of(page, size, Sort.by(sort).descending()));
        if (views.isEmpty()) {
            logger.warn("No views found for story with ID: {}", storyId);
            logger.error("Views retrieval failed for story with ID: {} due to no views existing", storyId);
            return  CollectionModel.of(List.of());
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
