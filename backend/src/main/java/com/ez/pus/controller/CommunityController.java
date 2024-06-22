package com.ez.pus.controller;

import com.ez.pus.UploadFiles.storage.StorageService;
import com.ez.pus.dto.CommunityDTO;
import com.ez.pus.dto.PostDTO;
import com.ez.pus.dto.StoryDTO;
import com.ez.pus.dto.UserDTO;
import com.ez.pus.enums.Course;
import com.ez.pus.enums.Major;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.*;
import com.ez.pus.repository.CommunityRepository;
import com.ez.pus.repository.PostRepository;
import com.ez.pus.repository.StoryRepository;
import com.ez.pus.repository.UserRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("/communities")
@RequiredArgsConstructor
public class CommunityController {

    private static final Logger log = LoggerFactory.getLogger(CommunityController.class);

    @Component
    static class CommunityModelAssembler implements RepresentationModelAssembler<Community, EntityModel<CommunityDTO>> {

        @NonNull
        @Override
        public EntityModel<CommunityDTO> toModel(@NonNull Community community) {
            log.trace("Assembling Community Model for Community ID: {}", community.getId());
            return EntityModel.of(CommunityDTO.fromCommunity(community),
                    linkTo(methodOn(CommunityController.class).one(community.getId())).withSelfRel(),
                    linkTo(methodOn(CommunityController.class).all()).withRel("communities"),
                    linkTo(methodOn(CommunityController.class).stories(community.getId())).withRel("stories"),
                    linkTo(methodOn(CommunityController.class).members(community.getId(), 0, 3, "id"))
                            .withRel("members"),
                    linkTo(methodOn(CommunityController.class).posts(community.getId(), 0, 3, "id")).withRel("posts"));
        }
    }

    private final CommunityRepository repository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StoryRepository storyRepository;
    private final CommunityModelAssembler assembler;
    private final StorageService storageService;

    @GetMapping
    public CollectionModel<EntityModel<CommunityDTO>> all() {
        log.debug("Fetching all communities");
        List<EntityModel<CommunityDTO>> communities = repository
                .findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if (communities.isEmpty()) {
            log.error("No communities found");
            return CollectionModel.of(List.of());
        }
        log.info("All communities fetched");
        return CollectionModel.of(communities, linkTo(methodOn(CommunityController.class).all()).withSelfRel());
    }

    @GetMapping("/{communityId}")
    public EntityModel<CommunityDTO> one(@PathVariable Long communityId) {
        log.debug("Fetching Community with ID: {}", communityId);
        Community community = repository.findById(communityId)
                .orElseThrow(() -> {
                    log.error("Community not found with ID: {}", communityId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Community", "ID", communityId);
                });
        log.info("Community fetched with ID: {}", communityId);
        return assembler.toModel(community);
    }

    @GetMapping("/{communityId}/users")
    public CollectionModel<UserDTO> members(@PathVariable Long communityId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "3") int size,
                                                         @RequestParam(defaultValue = "username") String sort) {
        log.debug("Fetching members for Community ID: {}", communityId);
        Community community = repository.findById(communityId)
                .orElseThrow(() -> {
                    log.error("Members not found for Community ID: {}", communityId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Community", "ID", communityId);
                });
        List<UserDTO> userDTOS = userRepository
                .findAllByCommunityName(community.getName(), PageRequest.of(page, size, Sort.by(sort).descending()))
                .stream()
                .map(UserDTO::fromUser)
                .collect(Collectors.toList());
        if (userDTOS.isEmpty()) {
            log.error("No members found for Community ID: {}", communityId);
            return CollectionModel.of(List.of());
        }
        log.info("Members fetched for Community ID: {}", communityId);
        List<User> nextPage = userRepository.findAllByCommunityName(community.getName(),
                PageRequest.of(page + 1, size, Sort.by(sort).descending()));
        return CollectionModel.of(userDTOS)
                .addIf(page > 0, () -> linkTo(methodOn(CommunityController.class).members(communityId, page - 1, size, sort)) .withRel("Previous Page"))
                .add(linkTo(methodOn(CommunityController.class).members(communityId, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(), () -> linkTo(methodOn(CommunityController.class).members(communityId, page + 1, size, sort)) .withRel("Next Page"));
    }

    @GetMapping("/{communityId}/posts")
    public CollectionModel<PostDTO> posts(@PathVariable Long communityId,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "3") int size,
                                          @RequestParam(defaultValue = "createdAt") String sort) {
        log.debug("Fetching posts for Community ID: {}", communityId);
        Community community = repository.findById(communityId)
                .orElseThrow(() -> {
                    log.error("Posts not found for Community ID: {}", communityId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Community", "ID", communityId);
                });
        List<PostDTO> posts = postRepository
                .findAllByCommunityName(community.getName(), PageRequest.of(page, size, Sort.by(sort).descending()))
                .stream()
                .map(PostDTO::fromPost)
                .collect(Collectors.toList());
        if (posts.isEmpty()) {
            log.error("No posts found for Community ID: {}", communityId);
            return CollectionModel.of(List.of());
        }
        log.info("Posts fetched for Community ID: {}", communityId);
        List<Post> nextPage = postRepository.findAllByCommunityName(community.getName(),
                PageRequest.of(page + 1, size, Sort.by(sort).descending()));
        return CollectionModel.of(posts)
                .addIf(page > 0,
                        () -> linkTo(methodOn(CommunityController.class).posts(communityId, page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(CommunityController.class).posts(communityId, page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(CommunityController.class).posts(communityId, page + 1, size, sort))
                                .withRel("Next Page"));
    }

    @GetMapping("/{communityId}/stories")
    public CollectionModel<StoryDTO> stories(@PathVariable Long communityId) {
        log.debug("Fetching stories for Community ID: {}", communityId);
        Community community = repository.findById(communityId)
                .orElseThrow(() -> {
                    log.error("Stories not found for Community ID: {}", communityId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Community", "ID", communityId);
                });
        List<StoryDTO> stories = storyRepository.findAllByCommunityName(community.getName())
                .stream()
                .map(StoryDTO::fromStory)
                .collect(Collectors.toList());
        if (stories.isEmpty()) {
            log.error("No stories found for Community ID: {}", communityId);
            return CollectionModel.of(List.of());
        }
        log.info("Stories fetched for Community ID: {}", communityId);
        return CollectionModel.of(stories)
                .add(linkTo(methodOn(CommunityController.class).stories(communityId)).withSelfRel());
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<?> delete(@PathVariable("communityId") Long communityId) {
        log.debug("Deleting Community with ID: {}", communityId);
        repository.deleteById(communityId);
        log.info("Community deleted with ID: {}", communityId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    ResponseEntity<?> add(@Valid @RequestBody CommunityDTO community) {
        log.debug("Adding new community with NAME: {}", community.getName().getName());
        EntityModel<CommunityDTO> communityEntityModel = assembler.toModel(Community.builder().name(community.getName()).build());
        log.info("New community added with NAME: {}", community.getName());
        return ResponseEntity.created(communityEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(communityEntityModel);
    }

    @GetMapping("/courses")
    public List<Map.Entry<String, String>> courses() {
        Community community = userRepository.findById(
                userRepository.findIdByUsername(
                        AuthTokenFilter.getCurrentUserByJwt()
                )
        ).get().getCommunity();
        return Arrays.stream(community.getName().getCourses()).collect(Collectors.toMap(Object::toString, Course::getName)).entrySet().stream().toList();
    }


    @GetMapping("/majors")
    public List<Map.Entry<String, String>> majors() {
        Community community = userRepository.findById(
                userRepository.findIdByUsername(
                        AuthTokenFilter.getCurrentUserByJwt()
                )
        ).get().getCommunity();
        return Arrays.stream(community.getName().getMajors()).collect(Collectors.toMap(Object::toString, Major::getName)).entrySet().stream().toList();
    }

    @PostMapping("/profile/{community_id}")
    @ResponseBody
    public ResponseEntity<String> handleFileUpload(
            @PathVariable Long community_id,
            @RequestParam("file") MultipartFile file) {
        String imageUrl = storageService.store(file);

        Community community = repository.findById(community_id)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Community", "id", community_id));
        community.setImageUrl(imageUrl);
        repository.save(community);
        return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
    }
}
