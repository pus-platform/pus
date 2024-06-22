package com.ez.pus.controller;

import com.ez.pus.dto.FollowerDTO;
import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Follower;
import com.ez.pus.model.Notification;
import com.ez.pus.model.User;
import com.ez.pus.repository.FollowerRepository;
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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequiredArgsConstructor
public class FollowerController {

    private static final Logger log = LoggerFactory.getLogger(FollowerController.class);
    private final UserRepository userRepository;

    @Component
    static class FollowerModelAssembler implements RepresentationModelAssembler<Follower, EntityModel<FollowerDTO>> {

        @NonNull
        @Override
        public EntityModel<FollowerDTO> toModel(@NonNull Follower follower) {
            log.trace("Assembling Follower Model for Follower ID: {} and Followed ID: {}", follower.getFollower().getId(), follower.getFollowed().getId());
            return EntityModel.of(FollowerDTO.fromFollower(follower),
                    linkTo(methodOn(FollowerController.class).one(follower.getFollower().getId(), follower.getFollowed().getId())).withSelfRel(),
                    linkTo(methodOn(FollowerController.class).oneFollowing(follower.getFollowed().getId(), follower.getFollower().getId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).one(follower.getFollowed().getId())).withRel("followed user"),
                    linkTo(methodOn(UserController.class).one(follower.getFollower().getId())).withRel("follower"));
        }
    }

    private final FollowerRepository repository;
    private final NotificationService notificationService;
    private final FollowerModelAssembler assembler;

    @GetMapping("/users/{userId}/followers/{followerId}")
    public EntityModel<FollowerDTO> one(@PathVariable Long userId, @PathVariable Long followerId) {
        log.debug("Fetching follower with Follower ID: {} for User ID: {}", followerId, userId);
        Follower follower = repository.findById(new Follower.FollowerId(followerId, userId))
                .orElseThrow(() -> {
                    log.error("Follower not found with Follower ID: {} for User ID: {}", followerId, userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("follower", "id", followerId);
                });
        log.info("Follower retrieved with Follower ID: {} for User ID: {}", followerId, userId);
        return assembler.toModel(follower);
    }

    @GetMapping("/users/{userId}/followers")
    public CollectionModel<EntityModel<FollowerDTO>> allFollowers(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "3") int size,
                                                                  @RequestParam(defaultValue = "followedAt") String sort) {
        log.debug("Listing all followers for User ID: {}", userId);
        List<Follower> nextPage = repository.findAllByFollowedId(userId, PageRequest.of(page + 1, size, Sort.by(sort)));
        CollectionModel<EntityModel<FollowerDTO>> followers = CollectionModel.of(
                        repository.findAllByFollowedId(userId, PageRequest.of(page, size, Sort.by(sort).descending())).stream()
                                .map(assembler::toModel)
                                .toList())
                .addIf(page > 0, () -> linkTo(methodOn(FollowerController.class).allFollowers(userId, page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(FollowerController.class).allFollowers(userId, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(), () -> linkTo(methodOn(FollowerController.class).allFollowers(userId, page + 1, size, sort)).withRel("Next Page"));
        log.info("Followers listed for User ID: {}", userId);
        if (followers.getContent().isEmpty()) {
            return CollectionModel.of(List.of());
        }
        return followers;
    }

    @PostMapping("/users/{userId}/followers")
    public ResponseEntity<?> newFollower(@Valid @RequestBody FollowerDTO newFollowerDTO) {
        log.debug("Creating new follower relationship");
        User follower = userRepository.findByUsername(AuthTokenFilter.getCurrentUserByJwt()).orElseThrow(() -> {
            log.error("Follower user not found");
            return new CustomExceptionHandling.InvalidArgumentException("User cannot follow themselves");
        });
        User followed = userRepository.findById(newFollowerDTO.getFollowed().getId()).orElseThrow(() -> {
            log.error("Followed user not found");
            return new CustomExceptionHandling.InvalidArgumentException("User cannot follow themselves");
        });
        if (Objects.equals(follower.getId(), followed.getId())) {
            log.error("User cannot follow themselves");
            throw new CustomExceptionHandling.InvalidArgumentException("User cannot follow themselves");
        }
        Follower newFollower = new Follower(follower, followed, newFollowerDTO.getFollowedAt());
        EntityModel<FollowerDTO> entityModel = assembler.toModel(repository.save(newFollower));
        notificationService.setFollower(newFollower);
        notificationService.sendNotification(NotificationType.FOLLOW_REQUEST);

        // Real-time notification
        notificationService.sendRealTimeNotification(NotificationDTO.fromNotification(
                Notification.builder()
                        .notificationType(NotificationType.FOLLOW_REQUEST)
                        .content(follower.getUsername() + " started following you")
                        .user(followed)
                        .notifiedAt(LocalDateTime.now())
                        .build()
        ));

        log.info("New follower relationship created");

        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }


    @DeleteMapping("/users/{userId}/followers/{followerId}")
    public ResponseEntity<Follower> deleteFollower(@PathVariable Long followerId, @PathVariable Long userId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())) &&
                !Objects.equals(followerId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot delete a follow for another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete a follow for another user");
        }
        log.debug("Deleting follower relationship with Follower ID: {} for User ID: {}", followerId, userId);
        repository.deleteById(new Follower.FollowerId(followerId, userId));
        log.info("Follower relationship deleted with Follower ID: {} for User ID: {}", followerId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{userId}/following")
    public CollectionModel<EntityModel<FollowerDTO>> allFollowing(@PathVariable Long userId,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "3") int size,
                                                                  @RequestParam(defaultValue = "followedAt") String sort) {
        log.debug("Listing all following for User ID: {}", userId);
        List<Follower> nextPage = repository.findAllByFollowerId(userId, PageRequest.of(page + 1, size, Sort.by(sort)));
        CollectionModel<EntityModel<FollowerDTO>> following = CollectionModel.of(
                        repository.findAllByFollowerId(userId, PageRequest.of(page, size, Sort.by(sort).descending())).stream()
                                .map(assembler::toModel)
                                .toList())
                .addIf(page > 0, () -> linkTo(methodOn(FollowerController.class).allFollowing(userId, page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(FollowerController.class).allFollowing(userId, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(), () -> linkTo(methodOn(FollowerController.class).allFollowing(userId, page + 1, size, sort)).withRel("Next Page"));
        if (following.getContent().isEmpty()) {
            log.error("No following found for User ID: {}", userId);
            return CollectionModel.of(List.of());
        }
        log.info("Following listed for User ID: {}", userId);
        return following;
    }

    @GetMapping("/users/{userId}/following/{followingId}")
    public EntityModel<FollowerDTO> oneFollowing(@PathVariable Long userId, @PathVariable Long followingId) {
        log.debug("Fetching following relationship with Following ID: {} for User ID: {}", followingId, userId);
        Follower follower = repository.findById(new Follower.FollowerId(userId, followingId))
                .orElseThrow(() -> {
                    log.error("Following not found with Following ID: {} for User ID: {}", followingId, userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Follower", "id", followingId);
                });
        log.info("Following retrieved with Following ID: {} for User ID: {}", followingId, userId);
        return assembler.toModel(follower);
    }
}
