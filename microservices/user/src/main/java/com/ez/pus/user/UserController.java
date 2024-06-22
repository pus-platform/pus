package com.ez.pus.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.follower.FollowerRepository;
import com.ez.pus.openFeign.UploadClient;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.security.jwt.JwtUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@RefreshScope
public class UserController {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(UserModelAssembler.class);
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final UserModelAssembler assembler;
    // TODO
    private final JwtUtils jwtUtils;
    private final UploadClient uploadClient;

    @Value("${message: Hello Default}")
    String message;

    @RequestMapping("/message")
    public String getMessage() {
        return message;
    }

    @GetMapping
    CollectionModel<EntityModel<UserDTO>> all(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size,
            @RequestParam(defaultValue = "id") String sort) {
        logger.trace("Fetching all users");
        logger.debug("Request received for fetching all users with page: {}, size: {}, sort: {}", page, size, sort);
        List<EntityModel<UserDTO>> users = userRepository.findAllUsers(PageRequest.of(page, size, Sort.by(sort)))
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if (users.isEmpty()) {
            logger.warn("No users found");
            throw new CustomExceptionHandling.ResourceNotFoundException("User", "all", null);
        }
        List<User> nextPage = userRepository.findAllUsers(PageRequest.of(page + 1, size, Sort.by(sort)));
        logger.info("Successfully fetched users");
        return CollectionModel.of(users)
                .addIf(page > 0,
                        () -> linkTo(methodOn(UserController.class).all(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(UserController.class).all(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(UserController.class).all(page + 1, size, sort)).withRel("Next Page"));
    }

    @GetMapping("/related")
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
                            if (Objects.equals(id, userId))
                                return false;
                            for (Long following : followings)
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
        return CollectionModel.of(relatedUsers)
                .addIf(page > 0,
                        () -> linkTo(methodOn(UserController.class).all(page - 1, size, sort)).withRel("Previous Page"))
                .add(linkTo(methodOn(UserController.class).all(page, size, sort)).withSelfRel());
    }

    @GetMapping("/{userId}")
    public EntityModel<UserDTO> one(@PathVariable Long userId) {
        logger.trace("Fetching user with ID: {}", userId);
        logger.debug("Request received for fetching user with ID: {}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", userId);
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "id", userId);
                });
        logger.info("Successfully fetched user with ID: {}", userId);
        return assembler.toModel(user);
    }

    @PutMapping("/{userId}")
    ResponseEntity<?> replaceUser(@Valid @RequestBody UserDTO newUser, @PathVariable Long userId) {
        logger.trace("Replacing user with ID: {}", userId);
        logger.debug("Request received for replacing user with ID: {}", userId);
        User updatedUser = userRepository.findById(userId)
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
                    if (newUser.getBio() != null)
                        user.setBio(newUser.getBio());
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    newUser.setId(userId);
                    return userRepository.save(User.builder()
                            .bio(newUser.getBio())
                            .email(newUser.getEmail())
                            .dob(newUser.getDob())
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

    @DeleteMapping("/{userId}")
    ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        logger.trace("Deleting user with ID: {}", userId);
        logger.debug("Request received for deleting user with ID: {}", userId);
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            logger.error("You cannot delete another user");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete another user");
        }
        userRepository.deleteById(userId);
        logger.info("User deleted with ID: {}", userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{username}/search")
    public ResponseEntity<EntityModel<UserDTO>> findByUsername(@PathVariable String username) {
        logger.trace("Fetching user with username: {}", username);
        logger.debug("Request received for fetching user with username: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("User not found with username: {}", username);
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "username", username);
                });

        logger.info("Successfully fetched user with username: {}", username);
        return ResponseEntity.ok(assembler.toModel(user));
    }

    @PostMapping("profile")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(
            @RequestParam("file") MultipartFile file) {

        String username = AuthTokenFilter.getCurrentUserByJwt();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("User", "username", username));

        String imageUrl = uploadClient.handleFileUpload(file);

        user.setImage(imageUrl);

        userRepository.save(user);

        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }

    @GetMapping("/searchUsers")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String searchTerm) {
        List<User> users = userRepository.searchUsersByUsernameOrFullname(searchTerm);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/current-user")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String token2 = AuthTokenFilter.getCurrentUserByJwt();
        // Optional<User> user = userRepository.findByUsername(token2);
        // return ResponseEntity.ok(UserDTO.fromUser(user.get()));
        String token = request.getHeader("Authorization").substring(7); // Remove "Bearer "
        System.out.println(token + " 1 : 2 " + token2);
        if (jwtUtils.validateJwtToken(token)) {
            String username = jwtUtils.getUserNameFromJwtToken(token);
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isPresent())
                return ResponseEntity.ok(UserDTO.fromUser(user.get()));
            else
                return ResponseEntity.badRequest().body("invalid username in token");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    @GetMapping("/current-user-by-jwt")
    public ResponseEntity<User> getTheUserByJwt() {
        String username = AuthTokenFilter.getCurrentUserByJwt();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("User", "username", username));
        System.out.println(user);
        return ResponseEntity.ok(user);
    }
}
