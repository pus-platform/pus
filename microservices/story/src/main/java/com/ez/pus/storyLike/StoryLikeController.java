package com.ez.pus.storyLike;

import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.story.Story;
import com.ez.pus.story.StoryController;
import com.ez.pus.story.StoryRepository;
import com.ez.pus.dto.UserDTO;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/stories")
public class StoryLikeController {

    private static final Logger log = LoggerFactory.getLogger(StoryLikeController.class);

    private final StoryLikeRepository storyLikeRepository;
    private final StoryRepository storyRepository;
    private final UserClient userClient;


    @Component
    private class StoryLikeModelAssembler
            implements RepresentationModelAssembler<StoryLike, EntityModel<StoryLikeDTO>> {
        @Override
        public @NonNull EntityModel<StoryLikeDTO> toModel(@NonNull StoryLike storyLike) {
            return EntityModel.of(StoryLikeDTO.fromStoryLike(storyLike),
                    linkTo(methodOn(StoryLikeController.class).getLike(storyLike.getStory().getId(),
                            storyLike.getUserId())).withSelfRel(),
                    linkTo(methodOn(StoryLikeController.class).getAllLikesForStory(storyLike.getStory().getId(), 0, 3,
                            "likedAt")).withRel("storyLikes"),
                    linkTo(methodOn(StoryController.class).getStoryById(storyLike.getStory().getId())).withRel("story")
                    // linkTo(userClient.one(storyLike.getUserId())).withRel("user")
                    );
        }
    }

    @GetMapping("/{storyId}/likes/{userId}")
    public EntityModel<StoryLikeDTO> getLike(@PathVariable Long storyId, @PathVariable Long userId) {
        log.trace("Entering getLike method");
        log.debug("Getting like for storyId: {}, userId: {}", storyId, userId);
        StoryLike.StoryLikeId id = new StoryLike.StoryLikeId(storyId, userId);
        StoryLike storyLike = storyLikeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Like not found for storyId: {}, userId: {}", storyId, userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("storyLike", "ID", storyId);
                });
        log.info("Successfully retrieved like for storyId: {}, userId: {}", storyId, userId);
        return new StoryLikeModelAssembler().toModel(storyLike);
    }

    @GetMapping("/{storyId}/likes")
    public CollectionModel<EntityModel<StoryLikeDTO>> getAllLikesForStory(@PathVariable Long storyId,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "3") int size,
                                                                          @RequestParam(defaultValue = "likedAt") String sort) {
        log.trace("Entering getAllLikesForStory method");
        log.debug("Getting all likes for storyId: {}", storyId);
        List<StoryLike> storyLikes = storyLikeRepository.findByStoryId(storyId,
                PageRequest.of(page, size, Sort.by(sort).descending()));
        if (storyLikes.isEmpty()) {
            log.warn("No likes found for storyId: {}", storyId);
            throw new CustomExceptionHandling.ResourceNotFoundException("story", "ID", storyId);
        }
        StoryLikeModelAssembler assembler = new StoryLikeModelAssembler();
        List<EntityModel<StoryLikeDTO>> likes = storyLikes.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        log.info("Successfully retrieved all likes for storyId: {}", storyId);
        return CollectionModel.of(likes,
                linkTo(methodOn(StoryLikeController.class).getAllLikesForStory(storyId, page, size, sort))
                        .withSelfRel());
    }

    @PostMapping("/{storyId}/likes")
    public ResponseEntity<EntityModel<StoryLikeDTO>> addLike(@Valid @RequestBody StoryLikeDTO storyLike) {
        log.trace("Entering addLike method");
        log.debug("Adding like for storyId: {}", storyLike.getStory());
        Story story = storyRepository.findById(storyLike.getStory())
                .orElseThrow(() -> new RuntimeException("Story not found"));
        UserDTO user = userClient.getCurrentUser();
        StoryLike newStoryLike = storyLikeRepository.save(StoryLike.builder()
                .story(story)
                .reaction(storyLike.getReaction())
                .userId(user.getId())
                .likedAt(storyLike.getLikedAt())
                .build());

        log.info("Successfully added like for storyId: {}", storyLike.getStory());
        EntityModel<StoryLikeDTO> entityModel = new StoryLikeModelAssembler().toModel(newStoryLike);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/{storyId}/likes/{userId}")
    public ResponseEntity<?> edit(@Valid @RequestBody StoryLikeDTO like, @PathVariable Long storyId,
                                  @PathVariable Long userId) {
        log.trace("Entering edit method for storyId: {}, userId: {}", storyId, userId);
        UserDTO user = userClient.getCurrentUser();
        if (!Objects.equals(userId, user.getId())) {
            log.error("You cannot edit story like for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot edit story like for another user");
        }
        StoryLike.StoryLikeId id = new StoryLike.StoryLikeId(userId, storyId);
        StoryLike edited = storyLikeRepository.findById(id)
                .map(storyLike -> {
                    storyLike.setReaction(like.getReaction());
                    return storyLikeRepository.save(storyLike);
                })
                .orElseGet(() -> storyLikeRepository.save(StoryLike.builder()
                        .story(storyRepository.findById(like.getStory()).get())
                        .reaction(like.getReaction())
                        .userId(user.getId())
                        .build()));
        log.info("Successfully edited like for storyId: {}, userId: {}", storyId, userId);
        EntityModel<StoryLikeDTO> entityModel = new StoryLikeModelAssembler().toModel(edited);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/{storyId}/likes/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long storyId, @PathVariable Long userId) {
        log.trace("Entering deleteLike method for storyId: {}, userId: {}", storyId, userId);
        if (!Objects.equals(userId, userClient.getCurrentUser().getId())) {
            log.error("You cannot delete story like for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete story like for another user");
        }
        StoryLike.StoryLikeId id = new StoryLike.StoryLikeId(userId, storyId);
        return storyLikeRepository.findById(id)
                .map(storyLike -> {
                    storyLikeRepository.delete(storyLike);
                    log.info("Successfully deleted like for storyId: {}, userId: {}", storyId, userId);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
