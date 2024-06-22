package com.ez.pus.controller;

import com.ez.pus.dto.GroupChatDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.GroupChat;
import com.ez.pus.model.GroupMember;
import com.ez.pus.repository.GroupChatRepository;
import com.ez.pus.repository.GroupMemberRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.jwt.AuthTokenFilter;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequiredArgsConstructor
public class GroupChatController {

    private static final Logger log = LoggerFactory.getLogger(GroupChatController.class);
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final GroupChatRepository repository;
    private final GroupChatModelAssembler assembler;

    @Component
    static class GroupChatModelAssembler implements RepresentationModelAssembler<GroupChat, EntityModel<GroupChatDTO>> {

        @NonNull
        @Override
        public EntityModel<GroupChatDTO> toModel(@NonNull GroupChat groupChat) {
            log.trace("Assembling model for GroupChat with ID: {}", groupChat.getId());
            return EntityModel.of(GroupChatDTO.fromGroupChat(groupChat),
                    linkTo(methodOn(GroupChatController.class).one(groupChat.getId())).withSelfRel(),
                    linkTo(methodOn(GroupMemberController.class).all(groupChat.getId())).withRel("members"));
        }
    }

    @GetMapping("/group-chats")
    public CollectionModel<EntityModel<GroupChatDTO>> userGroups() {
        Long userId = userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt());
        List<EntityModel<GroupChatDTO>> groups = groupMemberRepository.findAllByUserId(userId).stream()
                .map(GroupMember::getGroup)
                .map(assembler::toModel)
                .toList();
        if (groups.isEmpty()) {
            log.warn("No groups found for user with ID: {}", userId);
            return CollectionModel.of(List.of());
        }
        return CollectionModel.of(groups);
    }

    @GetMapping("/group-chats/{groupId}")
    public EntityModel<GroupChatDTO> one(@PathVariable Long groupId) {
        log.debug("Fetching GroupChat with ID: {}", groupId);
        if (!groupMemberRepository.findAllByGroupId(groupId) .stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot view group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot view group you are not in");
        }
        GroupChat groupChat = repository.findById(groupId)
                .orElseThrow(() -> {
                    log.error("GroupChat not found with ID: {}", groupId);
                    return new CustomExceptionHandling.ResourceNotFoundException("GroupChat", "id", groupId);
                });
        log.info("GroupChat fetched with ID: {}", groupId);
        return assembler.toModel(groupChat);
    }

    @PostMapping("/group-chats")
    public ResponseEntity<?> addGroupChat(@Valid @RequestBody GroupChatDTO groupChat) {
        log.debug("Creating new GroupChat");
        GroupChat savedGroupChat = repository.save(GroupChat.builder().name(groupChat.getName()).build());
        log.info("New GroupChat created with ID: {}", savedGroupChat.getId());
        EntityModel<GroupChatDTO> entityModel = assembler.toModel(savedGroupChat);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @PutMapping("/group-chats/{groupId}")
    ResponseEntity<?> renameGroupChat(@Valid @RequestBody GroupChatDTO newGroupChat, @PathVariable Long groupId) {
        log.debug("Updating GroupChat with ID: {}", groupId);
        if (!groupMemberRepository.findAllByGroupId(groupId) .stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot edit group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot edit group you are not in");
        }
        GroupChat updatedGroupChat = repository.findById(groupId)
                .map(groupChat -> {
                    groupChat.setName(newGroupChat.getName());
                    return repository.save(groupChat);
                })
                .orElseThrow(() -> {
                    log.error("GroupChat not found with ID: {}", groupId);
                    return new CustomExceptionHandling.ResourceNotFoundException("GroupChat", "id", groupId);
                });
        log.info("GroupChat updated with ID: {}", groupId);
        EntityModel<GroupChatDTO> entityModel = assembler.toModel(updatedGroupChat);
        return ResponseEntity
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(entityModel);
    }

    @DeleteMapping("/group-chats/{groupId}")
    public ResponseEntity<?> deleteGroupChatById(@PathVariable Long groupId) {
        log.debug("Attempting to delete GroupChat with ID: {}", groupId);
        if (!groupMemberRepository.findAllByGroupId(groupId) .stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot delete group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete group you are not in");
        }
        if (repository.existsById(groupId)) {
            repository.deleteById(groupId);
            log.info("GroupChat deleted with ID: {}", groupId);
            return ResponseEntity.ok().build();
        } else {
            log.warn("GroupChat with ID: {} not found for deletion", groupId);
            return ResponseEntity.noContent().build();
        }
    }
}
