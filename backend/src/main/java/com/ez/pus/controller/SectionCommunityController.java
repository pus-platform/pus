package com.ez.pus.controller;

import com.ez.pus.dto.SectionCommunityDTO;
import com.ez.pus.enums.GroupRole;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.*;
import com.ez.pus.repository.GroupChatRepository;
import com.ez.pus.repository.GroupMemberRepository;
import com.ez.pus.repository.SectionCommunityRepository;
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
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionCommunityController {
    private static final Logger log = LoggerFactory.getLogger(SectionCommunityController.class);

    @Getter
    static class UserId {
        Long id;
    }

    @Component
    static class SectionModelAssembler
            implements RepresentationModelAssembler<SectionCommunity, EntityModel<SectionCommunityDTO>> {

        @NonNull
        @Override
        public EntityModel<SectionCommunityDTO> toModel(@NonNull SectionCommunity sectionCommunity) {
            log.trace("Assembling Section Model for Section ID: {}", sectionCommunity.getId());
            return EntityModel.of(SectionCommunityDTO.fromSectionCommunity(sectionCommunity),
                    linkTo(methodOn(SectionCommunityController.class).one(sectionCommunity.getId())).withSelfRel(),
                    linkTo(methodOn(SectionCommunityController.class).all(0, 3, "course")).withRel("sections"),
                    linkTo(methodOn(SectionCommunityController.class).students(sectionCommunity.getId(), 0, 3,
                            "section")).withRel("students"));
        }
    }

    private final SectionCommunityRepository repository;
    private final GroupMemberRepository memberRepository;
    private final GroupChatRepository groupRepository;
    private final UserRepository userRepository;
    private final SectionCommunityController.SectionModelAssembler assembler;

    @GetMapping
    public CollectionModel<EntityModel<SectionCommunityDTO>> all(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "3") int size, @RequestParam(defaultValue = "course") String sort) {
        log.debug("Fetching all sections, page: {}, size: {}, sort: {}", page, size, sort);
        List<EntityModel<SectionCommunityDTO>> sections = repository
                .findAllSections(PageRequest.of(page, size, Sort.by(sort).descending()))
                .stream()
                .map(assembler::toModel)
                .toList();
        if (sections.isEmpty()) {
            log.error("No sections found");
            return CollectionModel.of(List.of());
        }
        log.info("All sections fetched");
        List<SectionCommunity> nextPage = repository
                .findAllSections(PageRequest.of(page + 1, size, Sort.by(sort).descending()));
        return CollectionModel.of(sections)
                .addIf(page > 0,
                        () -> linkTo(methodOn(SectionCommunityController.class).all(page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(SectionCommunityController.class).all(page, size, sort)).withSelfRel())
                .addIf(!nextPage.isEmpty(),
                        () -> linkTo(methodOn(SectionCommunityController.class).all(page + 1, size, sort))
                                .withRel("Next Page"));
    }

    @GetMapping("/{sectionId}")
    public EntityModel<SectionCommunityDTO> one(@PathVariable Long sectionId) {
        log.debug("Fetching Section with ID: {}", sectionId);
        SectionCommunity sectionCommunity = repository.findById(sectionId)
                .orElseThrow(() -> {
                    log.error("Section not found with ID: {}", sectionId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Section", "ID", sectionId);
                });
        log.info("Section fetched with ID: {}", sectionId);
        return assembler.toModel(sectionCommunity);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<?> delete(@PathVariable("sectionId") Long sectionId) {
        log.debug("Deleting Section with ID: {}", sectionId);
        repository.deleteById(sectionId);
        log.info("Section deleted with ID: {}", sectionId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping
    public ResponseEntity<?> add(@Valid @RequestBody SectionCommunityDTO sectionCommunityDTO) {
        log.debug("Adding new section for course: {}", sectionCommunityDTO.getCourse());
        SectionCommunity sectionCommunity = SectionCommunity.builder()
                .year(sectionCommunityDTO.getYear())
                .course(sectionCommunityDTO.getCourse())
                .division(sectionCommunityDTO.getDivision())
                .semester(sectionCommunityDTO.getSemester())
                .build();
        sectionCommunity.setSectionGroup(groupRepository.saveAndFlush(
                GroupChat.builder().name(sectionCommunity.getCourse().toString() + '_' + sectionCommunity.getDivision())
                        .createdAt(LocalDateTime.now()).build()));
        EntityModel<SectionCommunityDTO> sectionEntityModel = assembler.toModel(repository.save(sectionCommunity));
        log.info("New section added for course: {}", sectionCommunity.getCourse());
        return ResponseEntity.created(sectionEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(sectionEntityModel);
    }

    @GetMapping("/{sectionId}/students")
    public CollectionModel<EntityModel<User>> students(@PathVariable Long sectionId,
                                                       @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "3") int size,
                                                       @RequestParam(defaultValue = "username") String sort) {
        log.trace("Request to get all studies for sectionId: {}, page: {}, size: {}, sort: {}", sectionId, page, size,
                sort);
        SectionCommunity sectionCommunity = repository.findById(sectionId)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Section", "ID", sectionId));
        List<GroupMember> students = memberRepository.findAllByGroupId(sectionCommunity.getSectionGroup().getId());
        log.debug("Studies retrieved for section: {}", sectionId);
        CollectionModel<EntityModel<User>> studies = CollectionModel
                .of(students.stream().map(student -> EntityModel.of(student.getUser())).toList())
                .addIf(page > 0,
                        () -> linkTo(
                                methodOn(SectionCommunityController.class).students(sectionId, page - 1, size, sort))
                                .withRel("Previous Page"))
                .add(linkTo(methodOn(SectionCommunityController.class).students(sectionId, page, size, sort))
                        .withSelfRel())
                .addIf((page + 1) * size < students.size(),
                        () -> linkTo(
                                methodOn(SectionCommunityController.class).students(sectionId, page + 1, size, sort))
                                .withRel("Next Page"));
        log.info("students retrieved for section: {}", sectionId);
        if (studies.getContent().isEmpty()) {
            log.error("No students found for section: {}", sectionId);
            return CollectionModel.of(List.of());
        }
        return studies;
    }

    @PostMapping("/{sectionId}/students")
    ResponseEntity<?> addStudent(@PathVariable Long sectionId, @RequestBody UserId[] students) {
        log.debug("Request to create a new study for sectionId: {}", sectionId);
        List<EntityModel<GroupMember>> members = new ArrayList<>();
        for (UserId student : students) {
            User user = userRepository.findById(student.getId())
                    .orElseThrow(() -> {
                        log.error("User not found for user id : {}", student.getId());
                        return new CustomExceptionHandling.ResourceNotFoundException("User", "id", student.getId());
                    });
            SectionCommunity section = repository.findById(sectionId)
                    .orElseThrow(() -> {
                        log.error("Section not found for sectionId: {}", sectionId);
                        return new CustomExceptionHandling.ResourceNotFoundException("Section", "id", sectionId);
                    });
            if (memberRepository
                    .existsById(new GroupMember.GroupMemberId(user.getId(), section.getSectionGroup().getId()))) {
                log.info("student is already in the section");
                throw new RuntimeException("student is already in the section");
            }
            members.add(EntityModel
                    .of(memberRepository.saveAndFlush(
                            new GroupMember(user, section.getSectionGroup(), GroupRole.MEMBER, LocalDateTime.now())))
                    .add(linkTo(
                            methodOn(GroupMemberController.class).one(user.getId(), section.getSectionGroup().getId()))
                            .withSelfRel()));
            log.info("New student : {}, added to section {}", user.getUsername(), section);
        }
        CollectionModel<EntityModel<GroupMember>> collection = CollectionModel.of(members)
                .add(linkTo(methodOn(SectionCommunityController.class).addStudent(sectionId, students)).withSelfRel());
        return ResponseEntity.created(collection.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(collection);
    }

    @DeleteMapping("/{sectionId}/students/{studentId}")
    ResponseEntity<?> deleteStudent(@PathVariable Long sectionId, @PathVariable Long studentId) {
        log.info("Request to delete student with id: {} from section with id: {}", studentId, studentId);
        memberRepository.deleteById(
                new GroupMember.GroupMemberId(repository
                        .findById(sectionId)
                        .orElseThrow(
                                () -> new CustomExceptionHandling.ResourceNotFoundException("Section", "ID", sectionId))
                        .getSectionGroup().getId(), studentId));
        log.info("Study deleted for userId: {} and studentId: {}", sectionId, studentId);
        return ResponseEntity.noContent().build();
    }
}
