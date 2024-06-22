package com.ez.pus.controller;

import com.ez.pus.dto.GroupMemberDTO;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.GroupChat;
import com.ez.pus.model.GroupMember;
import com.ez.pus.model.User;
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
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequiredArgsConstructor
public class GroupMemberController {

    private static final Logger log = LoggerFactory.getLogger(GroupMemberController.class);

    @Component
    static class GroupMemberModelAssembler implements RepresentationModelAssembler<GroupMember, EntityModel<GroupMemberDTO>> {

        @NonNull
        @Override
        public EntityModel<GroupMemberDTO> toModel(@NonNull GroupMember groupMember) {
            log.trace("Assembling model for GroupMember with User ID: {}, GroupChat ID: {}", groupMember.getUser().getId(), groupMember.getGroup().getId());
            return EntityModel.of(GroupMemberDTO.fromGroupMember(groupMember),
                    linkTo(methodOn(GroupMemberController.class).one(groupMember.getUser().getId(), groupMember.getGroup().getId())).withSelfRel(),
                    linkTo(methodOn(GroupMemberController.class).all(groupMember.getGroup().getId())).withRel("group members"),
                    linkTo(methodOn(MessageController.class).oneGroupChat(groupMember.getUser().getId(), groupMember.getGroup().getId())).withRel("group messages"),
                    linkTo(methodOn(UserController.class).one(groupMember.getUser().getId())).withRel("user"),
                    linkTo(methodOn(GroupChatController.class).one(groupMember.getGroup().getId())).withRel("group")
            );
        }
    }

    private final GroupMemberRepository repository;
    private final GroupChatRepository groupChatRepository;
    private final UserRepository userRepository;
    private final GroupMemberModelAssembler assembler;

    @GetMapping("/group-chats/{groupChatId}/members")
    public CollectionModel<EntityModel<GroupMemberDTO>> all(@PathVariable Long groupChatId) {
        log.debug("Fetching all members for GroupChat ID: {}", groupChatId);
        if (!repository.findAllByGroupId(groupChatId).stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot view members of group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot view members of group you are not in");
        }

        List<EntityModel<GroupMemberDTO>> members = repository.findAll().stream()
                .filter(member -> member.getGroup().getId().equals(groupChatId))
                .map(assembler::toModel)
                .collect(Collectors.toList());
        if (members.isEmpty()) log.warn("No members found for GroupChat ID: {}", groupChatId);
        log.info("Fetched {} members for GroupChat ID: {}", members.size(), groupChatId);
        if (members.isEmpty()) {
            return CollectionModel.of(List.of());
        }
        return CollectionModel.of(members, linkTo(methodOn(GroupMemberController.class).all(groupChatId)).withSelfRel());
    }

    @GetMapping("/group-chats/{groupChatId}/members/{memberId}")
    public EntityModel<GroupMemberDTO> one(@PathVariable Long memberId, @PathVariable Long groupChatId) {
        log.debug("Fetching GroupMember with Member ID: {}, GroupChat ID: {}", memberId, groupChatId);
        if (!repository.findAllByGroupId(groupChatId).stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot view a member of group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot view a member of group you are not in");
        }
        GroupMember groupMember = repository.findById(new GroupMember.GroupMemberId(memberId, groupChatId))
                .orElseThrow(() -> {
                    log.error("GroupMember not found with Member ID: {}, GroupChat ID: {}", memberId, groupChatId);
                    return new CustomExceptionHandling.ResourceNotFoundException("GroupMember", "id", memberId);
                });
        log.info("GroupMember fetched with Member ID: {}, GroupChat ID: {}", memberId, groupChatId);
        return assembler.toModel(groupMember);
    }

    @PostMapping("/group-chats/{groupChatId}/members")
    public ResponseEntity<?> addMemberToGroupChat(@Valid @RequestBody GroupMemberDTO newGroupMember, @PathVariable Long groupChatId) {
        log.debug("Adding new member to GroupChat ID: {}", groupChatId);
        if (!repository.findAllByGroupId(groupChatId).stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot add members to a group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot add members to a group you are not in");
        }
        GroupChat groupChat = groupChatRepository.findById(groupChatId)
                .orElseThrow(() -> {
                    log.error("GroupChat not found with ID: {}", groupChatId);
                    return new CustomExceptionHandling.ResourceNotFoundException("GroupChat", "id", groupChatId);
                });

        User user = userRepository.findById(newGroupMember.getUser().getId())
                .orElseThrow(() -> {
                    log.error("User not found with ID: {}", newGroupMember.getUser());
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "id", newGroupMember.getUser());
                });

        if (repository.existsById(new GroupMember.GroupMemberId(newGroupMember.getUser().getId(), groupChatId))) {
            log.warn("Member already exists in GroupChat ID: {}", groupChatId);
            return ResponseEntity.badRequest().body("Member already exists in the group chat");
        }

        GroupMember savedGroupMember = repository.save(new GroupMember(user, groupChat, newGroupMember.getRole(), newGroupMember.getMemberSince()));
        log.info("New member added to GroupChat ID: {}", groupChatId);
        EntityModel<GroupMemberDTO> entityModel = assembler.toModel(savedGroupMember);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @PutMapping("/group-chats/{groupChatId}/members/{memberId}")
    public ResponseEntity<?> updateMemberInGroup(@Valid @RequestBody GroupMemberDTO newGroupMember, @PathVariable Long groupChatId, @PathVariable Long memberId) {
        log.debug("Updating member in GroupChat ID: {}, Member ID: {}", groupChatId, memberId);
        if (!repository.findAllByGroupId(groupChatId).stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot edit members of group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot edit members of group you are not in");
        }
        GroupMember updatedGroupMember = repository.findById(new GroupMember.GroupMemberId(memberId, groupChatId))
                .map(groupMember -> {
                    groupMember.setRole(newGroupMember.getRole());
                    groupMember.setMemberSince(newGroupMember.getMemberSince());
                    return repository.save(groupMember);
                })
                .orElseThrow(() -> {
                    log.error("GroupMember not found with Member ID: {}, GroupChat ID: {}", memberId, groupChatId);
                    return new CustomExceptionHandling.ResourceNotFoundException("GroupMember", "id", memberId);
                });
        log.info("Member updated in GroupChat ID: {}, Member ID: {}", groupChatId, memberId);
        EntityModel<GroupMemberDTO> entityModel = assembler.toModel(updatedGroupMember);
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @DeleteMapping("/group-chats/{groupChatId}/members/{memberId}")
    public ResponseEntity<?> deleteMemberFromGroup(@PathVariable Long groupChatId, @PathVariable Long memberId) {
        log.debug("Deleting member from GroupChat ID: {}, Member ID: {}", groupChatId, memberId);
        if (!repository.findAllByGroupId(groupChatId).stream()
                .map(groupMember -> groupMember.getUser().getId())
                .toList()
                .contains(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You cannot delete members of group you are not in");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot delete members of group you are not in");
        }
        if (repository.existsById(new GroupMember.GroupMemberId(memberId, groupChatId))) {
            repository.deleteById(new GroupMember.GroupMemberId(memberId, groupChatId));
            log.info("Member deleted from GroupChat ID: {}, Member ID: {}", groupChatId, memberId);
            return ResponseEntity.ok().build();
        } else {
            log.warn("Attempted to delete non-existent member from GroupChat ID: {}, Member ID: {}", groupChatId, memberId);
            return ResponseEntity.noContent().build();
        }
    }
}
