package com.ez.pus.controller;

import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.dto.StoryLikeDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Notification;
import com.ez.pus.model.Story;
import com.ez.pus.model.StoryLike;
import com.ez.pus.model.User;
import com.ez.pus.repository.StoryLikeRepository;
import com.ez.pus.repository.StoryRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.service.NotificationService;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequiredArgsConstructor
public class StoryLikeController {

    private static final Logger log = LoggerFactory.getLogger(StoryLikeController.class);

    private final StoryLikeRepository storyLikeRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;

    @Component
    private static class StoryLikeModelAssembler
            implements RepresentationModelAssembler<StoryLike, EntityModel<StoryLikeDTO>> {
        @Override
        public @NonNull EntityModel<StoryLikeDTO> toModel(@NonNull StoryLike storyLike) {
            return EntityModel.of(StoryLikeDTO.fromStoryLike(storyLike),
                    linkTo(methodOn(StoryLikeController.class).getLike(storyLike.getStory().getId(),
                            storyLike.getUser().getId())).withSelfRel(),
                    linkTo(methodOn(StoryLikeController.class).getAllLikesForStory(storyLike.getStory().getId(), 0, 3,
                            "likedAt")).withRel("storyLikes"),
                    linkTo(methodOn(StoryController.class).getStoryById(storyLike.getStory().getId())).withRel("story"),
                    linkTo(methodOn(UserController.class).one(storyLike.getUser().getId())).withRel("user"));
        }
    }

    @GetMapping("/stories/{storyId}/users/{userId}")
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

    @GetMapping("/stories/{storyId}/likes")
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
            return CollectionModel.of(List.of());
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

    @PostMapping("/stories/{storyId}/likes")
    public ResponseEntity<EntityModel<StoryLikeDTO>> addLike(@Valid @RequestBody StoryLikeDTO storyLike) {
        log.trace("Entering addLike method");
        log.debug("Adding like for storyId: {}", storyLike.getStory());
        Story story = storyRepository.findById(storyLike.getStory())
                .orElseThrow(() -> new RuntimeException("Story not found"));
        User user = userRepository.findById(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        StoryLike newStoryLike = storyLikeRepository.save(StoryLike.builder()
                .story(story)
                .reaction(storyLike.getReaction())
                .user(user)
                .likedAt(storyLike.getLikedAt())
                .build());
        notificationService.setStoryLike(newStoryLike);
        notificationService.sendNotification(NotificationType.STORY_LIKE);

        // Real-time notification
        notificationService.sendRealTimeNotification(NotificationDTO.fromNotification(
                Notification.builder()
                        .notificationType(NotificationType.STORY_LIKE)
                        .content(user.getUsername() + " liked your story")
                        .user(story.getUser())
                        .notifiedAt(LocalDateTime.now())
                        .build()
        ));

        log.info("Successfully added like for storyId: {}", storyLike.getStory());
        EntityModel<StoryLikeDTO> entityModel = new StoryLikeModelAssembler().toModel(newStoryLike);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/stories/{storyId}/users/{userId}")
    public ResponseEntity<?> edit(@Valid @RequestBody StoryLikeDTO like, @PathVariable Long storyId,
                                  @PathVariable Long userId) {
        log.trace("Entering edit method for storyId: {}, userId: {}", storyId, userId);
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
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
                        .user(userRepository.findById(like.getUser().getId()).get())
                        .build()));
        log.info("Successfully edited like for storyId: {}, userId: {}", storyId, userId);
        EntityModel<StoryLikeDTO> entityModel = new StoryLikeModelAssembler().toModel(edited);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/stories/{storyId}/users/{userId}")
    public ResponseEntity<?> deleteLike(@PathVariable Long storyId, @PathVariable Long userId) {
        log.trace("Entering deleteLike method for storyId: {}, userId: {}", storyId, userId);
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
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
