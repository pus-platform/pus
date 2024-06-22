package com.ez.pus.story;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.openFeign.UploadClient;
import com.ez.pus.openFeign.UserClient;
import com.ez.pus.storyLike.StoryLikeController;
import com.ez.pus.viewer.ViewerController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
@RefreshScope
public class StoryController {

    private static final Logger log = LoggerFactory.getLogger(StoryController.class);
    private final StoryRepository storyRepository;
    private final UserClient userClient;
    private final UploadClient uploadClient;

    public class StoryModelAssembler implements RepresentationModelAssembler<Story, EntityModel<StoryDTO>> {

        @Override
        public @NonNull EntityModel<StoryDTO> toModel(@NonNull Story story) {
            return EntityModel.of(StoryDTO.fromStory(story),
                    linkTo(methodOn(StoryController.class).getStoryById(story.getId())).withSelfRel(),
                    linkTo(methodOn(StoryController.class).getStoriesByFollowing(story.getUserId()))
                            .withRel("followings stories"),
                    linkTo(methodOn(StoryController.class).getAllStories()).withRel("stories"),
                    linkTo(methodOn(ViewerController.class).getViews(story.getId(), 0, 3, "viewedAt")).withRel("views"),
                    linkTo(methodOn(StoryLikeController.class).getAllLikesForStory(story.getId(), 0, 3, "likedAt"))
                            .withRel("likes")
            // linkTo(userClient.one(story.getUserId())).withRel("user")
            );
        }
    }

    @Value("${message: Hello Default}")
    String message;

    @RequestMapping("/message")
    public String getMessage() {
        return message;
    }

    @GetMapping("/{storyId}")
    public EntityModel<StoryDTO> getStoryById(@PathVariable Long storyId) {
        log.trace("getStoryById - start");
        log.debug("Retrieving story with ID: {}", storyId);
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> {
                    log.error("Story not found with ID: {}", storyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
                });
        log.info("Story retrieved with ID: {}", storyId);
        return new StoryModelAssembler().toModel(story);
    }

    @GetMapping("/users/{userId}")
    public CollectionModel<EntityModel<StoryDTO>> getStoriesByUserId(@PathVariable Long userId) {
        log.trace("getStoriesByUserId - start");
        log.debug("Retrieving stories for user ID: {}", userId);
        List<Story> storiesList = storyRepository.findByUserId(userId);
        if (storiesList.isEmpty()) {
            log.warn("No stories found for user ID: {}", userId);
            throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
        }
        List<EntityModel<StoryDTO>> stories = storiesList.stream()
                .map(new StoryModelAssembler()::toModel)
                .collect(Collectors.toList());
        log.info("Stories retrieved for user ID: {}", userId);
        return CollectionModel.of(stories,
                linkTo(methodOn(StoryController.class).getStoriesByUserId(userId)).withSelfRel());
    }

    @GetMapping("/users/{userId}/followings")
    public CollectionModel<EntityModel<StoryDTO>> getStoriesByFollowing(@PathVariable Long userId) {
        log.trace("getStoriesByFollowing - start");
        log.debug("Retrieving following IDs for user ID: {}", userId);
        List<Long> followingIds = userClient.allFollowing(userId, 0, 100000, "followedAt").getContent().stream()
                .map(follower -> follower.getContent().getFollowed().getId())
                .collect(Collectors.toList());

        if (followingIds.isEmpty()) {
            log.warn("No followings found for user ID: {}", userId);
            throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
        }
        List<Story> storiesList = storyRepository.findAllByUserIdIn(followingIds);
        if (storiesList.isEmpty()) {
            log.warn("No stories found for the followings of user ID: {}", userId);
            throw new CustomExceptionHandling.ResourceNotFoundException("User", "ID", userId);
        }
        List<EntityModel<StoryDTO>> stories = storiesList.stream()
                .map(new StoryModelAssembler()::toModel)
                .collect(Collectors.toList());
        log.info("Stories retrieved for the followings of user ID: {}", userId);
        return CollectionModel.of(stories,
                linkTo(methodOn(StoryController.class).getStoriesByFollowing(userId)).withSelfRel());
    }

    @GetMapping
    public CollectionModel<EntityModel<StoryDTO>> getAllStories() {
        log.trace("getAllStories - start");
        List<EntityModel<StoryDTO>> stories = storyRepository.findAll().stream()
                .map(new StoryModelAssembler()::toModel)
                .collect(Collectors.toList());
        if (stories.isEmpty()) {
            log.warn("No stories found");
            throw new CustomExceptionHandling.ResourceNotFoundException("Story", "all", null);
        }
        log.info("Retrieved all stories");
        return CollectionModel.of(stories, linkTo(methodOn(StoryController.class).getAllStories()).withSelfRel());
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<EntityModel<StoryDTO>> addStory(
            @RequestPart("story") String storyJson,
            @RequestPart("file") MultipartFile file) throws JsonProcessingException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        StoryDTO storyDTO = objectMapper.readValue(storyJson, StoryDTO.class);

        String imageUrl = uploadClient.handleFileUpload(file);

        Story newStory = Story.builder()
                .view(storyDTO.getView())
                .userId(userClient.getCurrentUser().getId())
                .createdAt(storyDTO.getCreatedAt())
                .imageUrl(imageUrl)
                .build();

        newStory = storyRepository.save(newStory);

        EntityModel<StoryDTO> entityModel = new StoryModelAssembler().toModel(newStory);

        return ResponseEntity.created(entityModel.getRequiredLink("self").toUri()).body(entityModel);
    }

    @DeleteMapping("/{storyId}")
    public ResponseEntity<?> deleteStory(@PathVariable Long storyId) {
        log.trace("deleteStory - start");
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> {
                    log.error("Story not found with ID: {}", storyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
                });
        if (!Objects.equals(story.getUserId(), userClient.getCurrentUser().getId())) {
            log.error("You cannot delete story for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete story for another user");
        }
        storyRepository.delete(story);
        log.info("Story deleted with ID: {}", storyId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{storyId}")
    public ResponseEntity<EntityModel<StoryDTO>> updateStory(@PathVariable Long storyId,
            @RequestBody StoryDTO newStory) {
        log.trace("updateStory - start");
        Story updatedStory = storyRepository.findById(storyId)
                .map(story -> {
                    if (!Objects.equals(story.getUserId(), userClient.getCurrentUser().getId())) {
                        log.error("You cannot edit story for another user");
                        throw new CustomExceptionHandling.InvalidArgumentException(
                                "You cannot edit story for another user");
                    }
                    story.setView(newStory.getView());
                    return storyRepository.save(story);
                })
                .orElseThrow(() -> {
                    log.error("Story not found with ID: {}", storyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
                });
        log.info("Story updated with ID: {}", updatedStory.getId());
        EntityModel<StoryDTO> entityModel = new StoryModelAssembler().toModel(updatedStory);
        return ResponseEntity.created(entityModel.getRequiredLink("self")
                .toUri()).body(entityModel);
    }
}