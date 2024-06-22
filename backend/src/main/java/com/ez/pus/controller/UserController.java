package com.ez.pus.controller;

import com.ez.pus.UploadFiles.storage.StorageService;
import com.ez.pus.dto.UserDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.repository.CommunityRepository;
import com.ez.pus.repository.FollowerRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.model.User;
import com.ez.pus.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequiredArgsConstructor
public class UserController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserModelAssembler.class);
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;
    private final FollowerRepository followerRepository;
    private final StorageService storageService;
    private final UserRepository repository;
    private final UserModelAssembler assembler;

    @Component
    static class UserModelAssembler implements RepresentationModelAssembler<User, EntityModel<UserDTO>> {

        @NonNull
        @Override
        public EntityModel<UserDTO> toModel(@NonNull User user) {
            return EntityModel.of(UserDTO.fromUser(user),
                    linkTo(methodOn(UserController.class).one(user.getId())).withSelfRel(),
                    linkTo(methodOn(UserController.class).all(0, 3, "id")).withRel("users"),
                    linkTo(methodOn(PostController.class).getAllPostsByUserId(user.getId(), 0, 3, "createdAt"))
                            .withRel("posts"),
                    linkTo(methodOn(StoryController.class).getStoriesByUserId(user.getId())).withRel("user stories"),
//                    linkTo(methodOn(FollowerController.class).allFollowing(user.getId(), 0, 3, "followedAt"))
//                            .withRel("following"),
                    linkTo(methodOn(FollowerController.class).allFollowers(user.getId(), 0, 3, "followedAt"))
                            .withRel("followers"),
//                    linkTo(methodOn(MessageController.class).all(user.getId())).withRel("messages"),
                    linkTo(methodOn(NotificationController.class).all(0, 3, "notifiedAt"))
                            .withRel("notifications"),
                    linkTo(methodOn(BookmarkController.class).all(0, 3, "savedAt")).withRel("bookmarks"));
        }
    }

    @GetMapping("/users")
    CollectionModel<EntityModel<UserDTO>> all(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "3") int size,
                                              @RequestParam(defaultValue = "id") String sort) {
        logger.trace("Fetching all users");
        logger.debug("Request received for fetching all users with page: {}, size: {}, sort: {}", page, size, sort);
        List<EntityModel<UserDTO>> users = repository.findAllUsers(PageRequest.of(page, size, Sort.by(sort))).stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if (users.isEmpty()) {
            logger.warn("No users found");
            return CollectionModel.of(List.of());
        }
        List<User> nextPage = repository.findAllUsers(PageRequest.of(page + 1, size, Sort.by(sort)));
        logger.info("Successfully fetched users");
        return CollectionModel.of(users)
                .addIf(page > 0,
                        () -> linkTo(methodOn(UserController.class).all(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(UserController.class).all(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(UserController.class).all(page + 1, size, sort)).withRel("Next Page"));
    }

    @GetMapping("/users/related")
    CollectionModel<EntityModel<UserDTO>> related(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "3") int size,
                                                  @RequestParam(defaultValue = "follower_id") String sort) {
        logger.info("Fetching Related Users");

        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        List<Long> followings = followerRepository.findAllByFollowerId(
                        userId,
                        PageRequest.of(page, size, Sort.by(sort))).stream()
                .map(follower -> follower.getFollowed().getId())
                .toList();
        Set<Long> related = Set.copyOf(
                followerRepository.findAllByFollowerIdIn(followings)
                        .stream()
                        .map(follower -> follower.getFollowed().getId())
                        .filter(id -> {
                            if (Objects.equals(id, userId)) return false;
                            for(Long following : followings)
                                if (Objects.equals(id, following))
                                    return false;
                            return true;
                        })
                        .toList());

        logger.info("{}", related);
        logger.info("Related Users Fetched successfully");
        List<EntityModel<UserDTO>> relatedUsers = userRepository.findAllByIdIn(related).stream()
                .map(assembler::toModel)
                .toList();
        if (relatedUsers.isEmpty()) {
            logger.warn("No related users found");
            return CollectionModel.of(List.of());
        }
        return CollectionModel.of(relatedUsers)
                .addIf(page > 0, () -> linkTo(methodOn(UserController.class).all(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(UserController.class).all(page, size, sort)).withSelfRel());
    }

    @GetMapping("/users/{userId}")
    EntityModel<UserDTO> one(@PathVariable Long userId) {
        logger.trace("Fetching user with ID: {}", userId);
        logger.debug("Request received for fetching user with ID: {}", userId);
        User user = repository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "id", userId);
                });
        logger.info("Successfully fetched user with ID: {}", userId);
        return assembler.toModel(user);
    }

    @PutMapping("/users/{userId}")
    ResponseEntity<?> replaceUser(@Valid @RequestBody UserDTO newUser, @PathVariable Long userId) {
        logger.trace("Replacing user with ID: {}", userId);
        logger.debug("Request received for replacing user with ID: {}", userId);
        User updatedUser = repository.findById(userId)
                .map(user -> {
                    if (!Objects.equals(user.getUsername(), AuthTokenFilter.getCurrentUserByJwt())) {
                        logger.error("You cannot edit another user");
                        throw new CustomExceptionHandling.InvalidArgumentException("You cannot edit another user");
                    }
                    if (newUser.getUsername() != null)
                        user.setUsername(newUser.getUsername());
                    if (newUser.getFullname() != null)
                        user.setFullname(newUser.getFullname());
                    if (newUser.getDob() != null)
                        user.setDob(newUser.getDob());
                    if (newUser.getPassword() != null)
                        user.setPassword(newUser.getPassword());
                    if (newUser.getEmail() != null)
                        user.setEmail(newUser.getEmail());
                    if (newUser.getIsActive() != null)
                        user.setIsActive(newUser.getIsActive());
                    if (newUser.getBio() != null)
                        user.setBio(newUser.getBio());
                    if (newUser.getCommunity() != null)
                        user.setCommunity(communityRepository.findByName(newUser.getCommunity()).get());
                    return repository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(userId);
                    return repository.save(User.builder()
                            .bio(newUser.getBio())
                            .email(newUser.getEmail())
                            .dob(newUser.getDob())
                            .community(communityRepository.findByName(newUser.getCommunity()).get())
                            .isActive(newUser.getIsActive())
                            .fullname(newUser.getFullname())
                            .username(newUser.getUsername())
                            .gender(newUser.getGender())
                            .password(newUser.getPassword())
                            .build());
                });

        EntityModel<UserDTO> entityModel = assembler.toModel(updatedUser);
        logger.info("User replaced with ID: {}", Objects.requireNonNull(entityModel.getContent()).getId());
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/users/{userId}")
    ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        logger.trace("Deleting user with ID: {}", userId);
        logger.debug("Request received for deleting user with ID: {}", userId);
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            logger.error("You cannot delete another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete another user");
        }
        repository.deleteById(userId);
        logger.info("User deleted with ID: {}", userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/{username}/search")
    public ResponseEntity<?> findByUsername(@PathVariable String username) {
        logger.trace("Fetching user with username: {}", username);
        logger.debug("Request received for fetching user with username: {}", username);

        Optional<User> user = repository.findByUsername(username);
        if (user.isEmpty())
            return ResponseEntity.badRequest().body("username not found");


        logger.info("Successfully fetched user with username: {}", username);
        return ResponseEntity.ok(assembler.toModel(user.get()));
    }
    @PostMapping("/usersprofile")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("file") MultipartFile file) {

        String username = AuthTokenFilter.getCurrentUserByJwt();

        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty())
            return ResponseEntity.badRequest().body("not found");

        String imageUrl = storageService.store(file);

        user.get().setImage(imageUrl);

        userRepository.save(user.get());

        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }

    @GetMapping("/searchUsers")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String searchTerm) {
        List<User> users = userRepository.searchUsersByUsernameOrFullname(searchTerm);
        return ResponseEntity.ok(users);
    }
}
