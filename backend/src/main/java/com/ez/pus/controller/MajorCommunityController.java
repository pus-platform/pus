package com.ez.pus.controller;

import com.ez.pus.dto.MajorCommunityDTO;
import com.ez.pus.enums.GroupRole;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.GroupChat;
import com.ez.pus.model.GroupMember;
import com.ez.pus.model.MajorCommunity;
import com.ez.pus.model.User;
import com.ez.pus.repository.GroupChatRepository;
import com.ez.pus.repository.GroupMemberRepository;
import com.ez.pus.repository.MajorCommunityRepository;
import com.ez.pus.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.Getter;
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
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("/major-communities")
@RequiredArgsConstructor
public class MajorCommunityController {

    private static final Logger log = LoggerFactory.getLogger(MajorCommunityController.class);

    @Getter
    static class UserId {
        Long id;
    }

    @Component
    static class MajorModelAssembler
            implements RepresentationModelAssembler<MajorCommunity, EntityModel<MajorCommunityDTO>> {
        @NonNull
        @Override
        public EntityModel<MajorCommunityDTO> toModel(@NonNull MajorCommunity majorCommunity) {
            log.trace("Assembling Major Model for Major ID: {}", majorCommunity.getId());
            return EntityModel.of(MajorCommunityDTO.fromMajorCommunity(majorCommunity),
                    linkTo(methodOn(MajorCommunityController.class).one(majorCommunity.getId())).withSelfRel(),
                    linkTo(methodOn(MajorCommunityController.class).all(0, 3, "course")).withRel("majors"),
                    linkTo(methodOn(MajorCommunityController.class).students(majorCommunity.getId(), 0, 3, "major"))
                            .withRel("students"));
        }
    }

    private final MajorCommunityRepository repository;
    private final GroupMemberRepository memberRepository;
    private final GroupChatRepository groupRepository;
    private final UserRepository userRepository;
    private final MajorModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<MajorCommunityDTO>> all(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "3") int size, @RequestParam(defaultValue = "major") String sort) {
        log.debug("Fetching all majors, page: {}, size: {}, sort: {}", page, size, sort);
        List<EntityModel<MajorCommunityDTO>> majors = repository
                .findAllMajors(PageRequest.of(page, size, Sort.by(sort).descending()))
                .stream()
                .map(assembler::toModel)
                .toList();
        if (majors.isEmpty()) {
            log.error("No majors found");
            return CollectionModel.of(List.of());
        }
        log.info("All majors fetched");
        List<MajorCommunity> nextPage = repository
                .findAllMajors(PageRequest.of(page + 1, size, Sort.by(sort).descending()));
        return CollectionModel.of(majors)
                .addIf(page > 0,
                        () -> linkTo(methodOn(MajorCommunityController.class).all(page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(MajorCommunityController.class).all(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(MajorCommunityController.class).all(page + 1, size, sort))
                                .withRel("Next Page"));
    }

    @GetMapping("/{majorId}")
    public EntityModel<MajorCommunityDTO> one(@PathVariable Long majorId) {
        log.debug("Fetching Major with ID: {}", majorId);
        MajorCommunity majorCommunity = repository.findById(majorId)
                .orElseThrow(() -> {
                    log.error("Major not found with ID: {}", majorId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Major", "ID", majorId);
                });
        log.info("Major fetched with ID: {}", majorId);
        return assembler.toModel(majorCommunity);
    }

    @DeleteMapping("/{majorId}")
    public ResponseEntity<?> delete(@PathVariable("majorId") Long majorId) {
        log.debug("Deleting Major with ID: {}", majorId);
        repository.deleteById(majorId);
        log.info("Major deleted with ID: {}", majorId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody MajorCommunityDTO majorCommunityDTO) {
        log.debug("Adding new major: {}", majorCommunityDTO);
        MajorCommunity majorCommunity = MajorCommunity.builder()
                .major(majorCommunityDTO.getMajor())
                .year(majorCommunityDTO.getYear())
                .build();
        majorCommunity.setMajorGroup(groupRepository.saveAndFlush(
                GroupChat.builder().name(majorCommunity.getMajor().toString() + '_' + majorCommunity.getYear())
                        .createdAt(LocalDateTime.now()).build()));
        EntityModel<MajorCommunityDTO> majorEntityModel = assembler.toModel(repository.save(majorCommunity));
        log.info("New major added: {}", majorCommunity);
        return ResponseEntity.created(majorEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(majorEntityModel);
    }

    @GetMapping("/{majorId}/students")
    public CollectionModel<EntityModel<User>> students(@PathVariable Long majorId,
                                                       @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
                                                       @RequestParam(defaultValue = "username") String sort) {
        log.trace("Request to get all studies for majorId: {}, page: {}, size: {}, sort: {}", majorId, page, size,
                sort);
        MajorCommunity majorCommunity = repository.findById(majorId)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Major", "ID", majorId));
        List<GroupMember> students = memberRepository.findAllByGroupId(majorCommunity.getMajorGroup().getId());
        log.debug("Studies retrieved for major: {}", majorId);
        CollectionModel<EntityModel<User>> studies = CollectionModel
                .of(students.stream().map(student -> EntityModel.of(student.getUser())).toList())
                .addIf(page > 0,
                        () -> linkTo(methodOn(MajorCommunityController.class).students(majorId, page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(MajorCommunityController.class).students(majorId, page, size, sort)).withSelfRel())
                .addIf((page + 1) * size < students.size(),
                        () -> linkTo(methodOn(MajorCommunityController.class).students(majorId, page + 1, size, sort))
                                .withRel("Next Page"));
        log.info("students retrieved for major: {}", majorId);
        if (studies.getContent().isEmpty()) {
            log.error("No students found for major: {}", majorId);
            return CollectionModel.of(List.of());
        }
        return studies;
    }

    @PostMapping("/{majorId}/students")
    ResponseEntity<?> addStudent(@PathVariable Long majorId, @RequestBody UserId[] students) {
        log.debug("Request to create a new study for majorId: {}", majorId);
        List<EntityModel<GroupMember>> members = new ArrayList<>();
        for (UserId student : students) {
            User user = userRepository.findById(student.getId())
                    .orElseThrow(() -> {
                        log.error("User not found for user id : {}", student.getId());
                        return new CustomExceptionHandling.ResourceNotFoundException("User", "id", student.getId());
                    });
            MajorCommunity major = repository.findById(majorId)
                    .orElseThrow(() -> {
                        log.error("Major not found for majorId: {}", majorId);
                        return new CustomExceptionHandling.ResourceNotFoundException("Major", "id", majorId);
                    });
            if (memberRepository
                    .existsById(new GroupMember.GroupMemberId(user.getId(), major.getMajorGroup().getId()))) {
                log.info("student is already in the major");
                throw new RuntimeException("student is already in the major");
            }
            members.add(EntityModel
                    .of(memberRepository.saveAndFlush(
                            new GroupMember(user, major.getMajorGroup(), GroupRole.MEMBER, LocalDateTime.now())))
                    .add(linkTo(methodOn(GroupMemberController.class).one(user.getId(), major.getMajorGroup().getId()))
                            .withSelfRel()));
            log.info("New student : {}, added to major {}", user.getUsername(), major);
        }
        CollectionModel<EntityModel<GroupMember>> collection = CollectionModel.of(members)
                .add(linkTo(methodOn(MajorCommunityController.class).addStudent(majorId, students)).withSelfRel());
        return ResponseEntity.created(collection.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(collection);
    }

    @DeleteMapping("/{majorId}/students/{studentId}")
    ResponseEntity<?> deleteStudent(@PathVariable Long majorId, @PathVariable Long studentId) {
        log.info("Request to delete student with id: {} from major with id: {}", studentId, studentId);
        memberRepository.deleteById(
                new GroupMember.GroupMemberId(repository
                        .findById(majorId)
                        .orElseThrow(
                                () -> new CustomExceptionHandling.ResourceNotFoundException("Major", "ID", majorId))
                        .getMajorGroup().getId(), studentId));
        log.info("Study deleted for userId: {} and studentId: {}", majorId, studentId);
        return ResponseEntity.noContent().build();
    }
}
