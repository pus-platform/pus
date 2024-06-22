package com.ez.pus.controller;

import com.ez.pus.dto.MessageDTO;
import com.ez.pus.enums.MessageReceiver;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.repository.GroupMemberRepository;
import com.ez.pus.repository.MessageRepository;
import com.ez.pus.repository.*;
import com.ez.pus.model.GroupMember;
import com.ez.pus.model.Message;
import com.ez.pus.model.User;
import com.ez.pus.service.NotificationService;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RequiredArgsConstructor
@RestController
public class MessageController {

    private static final Logger log = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private SimpMessagingTemplate messageTemplate;


    @Component
    static class MessageModelAssembler implements RepresentationModelAssembler<Message, EntityModel<MessageDTO>> {

        @NonNull
        @Override
        public EntityModel<MessageDTO> toModel(@NonNull Message message) {
            Link rec = (message.getReceiverType() == MessageReceiver.GROUP_CHAT ?
                    linkTo(methodOn(GroupChatController.class).one(message.getReceiverGroup().getId())).withRel("group") :
                    linkTo(methodOn(UserController.class).one(message.getReceiverUser().getId())).withRel("receiver"));
            return EntityModel.of(MessageDTO.fromMessage(message),
                    linkTo(methodOn(MessageController.class).one(message.getSender().getId(), message.getId())).withSelfRel(),
                    linkTo(methodOn(MessageController.class).all(message.getSender().getId())).withRel("messages"),
                    linkTo(methodOn(UserController.class).one(message.getSender().getId())).withRel("sender"),
                    rec);
        }
    }

    private final MessageRepository repository;
    private final MessageModelAssembler assembler;
    private final GroupMemberRepository groupMemberRepository;
    final UserRepository userRepository;
    final GroupChatRepository groupChatRepository;
    private final NotificationService notificationService;

    @GetMapping("/users/{userId}/messages")
    public CollectionModel<CollectionModel<EntityModel<MessageDTO>>> all(@PathVariable Long userId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("Cannot retrieve messages for another user");
            throw new CustomExceptionHandling.ResourceNotFoundException("Message", "ID", userId);
        }

        log.info("Getting all messages for user with ID: {}", userId);

        List<GroupMember> groupMembers = groupMemberRepository.findAllByUserId(userId);
        List<CollectionModel<EntityModel<MessageDTO>>> groupMessages = groupMembers.stream()
                .map(groupMember -> {
                    List<Message> messages = groupMember.getGroup().getMessages();
                    List<EntityModel<MessageDTO>> messageModels = messages.stream()
                            .map(assembler::toModel)
                            .toList();
                    return CollectionModel.of(messageModels);
                })
                .collect(Collectors.toList());
        if (groupMembers.isEmpty()) {
            log.info("Returning all messages for user with ID: {}", userId);
            return CollectionModel.of(List.of());
        }
        List<Message> sentMessages = repository.findAllBySenderId(userId);
        List<Message> receivedMessages = repository.findAllByReceiverUserId(userId);
        List<Message> allMessages = new ArrayList<>();

        allMessages.addAll(sentMessages);
        allMessages.addAll(receivedMessages);
        allMessages.sort(Comparator.comparing(Message::getSentAt).reversed());

        Map<User, List<Message>> groupedMessages = allMessages.stream()
                .collect(Collectors.groupingBy(
                        message -> message.getSender().getId().equals(userId) ?
                                message.getReceiverUser() :
                                message.getSender()));
        List<CollectionModel<EntityModel<MessageDTO>>> personalMessages = new ArrayList<>();
        for (Map.Entry<User, List<Message>> entry : groupedMessages.entrySet()) {
            List<EntityModel<MessageDTO>> messageModels = entry.getValue().stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());
            personalMessages.add(CollectionModel.of(messageModels));
        }

        groupMessages.addAll(personalMessages);
        log.info("Returning all messages for user with ID: {}", userId);
        return CollectionModel.of(groupMessages);
    }

    @GetMapping("/users/{userId}/messages/group/{groupId}")
    public CollectionModel<EntityModel<MessageDTO>> oneGroupChat(@PathVariable Long userId, @PathVariable Long groupId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("Cannot retrieve messages for another user");
            throw new CustomExceptionHandling.ResourceNotFoundException("Message", "ID", userId);
        }
        log.info("Getting all messages for user with ID: {} in group with ID: {}", userId, groupId);
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("GroupMember", "userId, groupId", userId + ", " + groupId));
        log.info("GroupMember found: {}", groupMember.toString());
        List<Message> messages = groupMember.getGroup().getMessages();

        if (messages.isEmpty()) {
            log.info("Returning all messages for user with ID: {} in group with ID: {}", userId, groupId);
            return CollectionModel.of(List.of());
        }
        messages.sort(Comparator.comparing(Message::getSentAt).reversed());

        for (Message message : messages) {
            if (!message.getSender().getId().equals(userId)) {
                message.setIsRead(true);
                repository.save(message);
            }
        }
        List<EntityModel<MessageDTO>> messageModels = messages.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        log.info("Returning all messages for user with ID: {} in group with ID: {}", userId, groupId);
        return CollectionModel.of(messageModels);
    }

    @GetMapping("/users/{userId}/messages/user/{otherUserId}")
    public CollectionModel<EntityModel<MessageDTO>> one(@PathVariable Long userId, @PathVariable Long otherUserId) {
        if (!Objects.equals(userId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())) &&
                !Objects.equals(otherUserId, userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("Cannot retrieve messages for another user");
            throw new CustomExceptionHandling.ResourceNotFoundException("Message", "ID", userId);
        }
        log.info("Getting all messages between user with ID: {} and user with ID: {}", userId, otherUserId);
        List<Message> messages = repository.findAllByReceiverUserIdAndSenderId(userId, otherUserId);
        messages.addAll(repository.findAllByReceiverUserIdAndSenderId(otherUserId, userId));
        messages.sort(Comparator.comparing(Message::getSentAt).reversed());

        for (Message message : messages) {
            if (!message.getSender().getId().equals(userId)) {
                message.setIsRead(true);
                repository.save(message);
            }
        }
        List<EntityModel<MessageDTO>> messageModels = messages.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        log.info("Returning all messages between user with ID: {} and user with ID: {}", userId, otherUserId);
        return CollectionModel.of(messageModels);
    }

    @MessageMapping("/chat")
    public void newMessage(@Valid @Payload MessageDTO newMessageDTO) {
        log.info("Creating new message: {}", newMessageDTO.getMessageContent());
        Message newMessage = Message.builder()
                .isRead(newMessageDTO.getIsRead())
                .messageContent(newMessageDTO.getMessageContent())
                .receiverType(newMessageDTO.getReceiverType())
                .build();

        Long userId = newMessageDTO.getSender().getId();
        newMessage.setSender(userRepository.findById(userId).orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Sender", "ID", userId)));
        if (newMessageDTO.getReceiverType() == MessageReceiver.USER)
            newMessage.setReceiverUser(userRepository.findById(newMessageDTO.getReceiverUser().getId()).get());
        else
            newMessage.setReceiverGroup(groupChatRepository.findById(newMessageDTO.getReceiverGroup()).get());

        EntityModel<MessageDTO> entityModel = assembler.toModel(repository.save(newMessage));
        if (newMessage.getReceiverType() == MessageReceiver.GROUP_CHAT)
            messageTemplate.convertAndSend("/topic/group-" + newMessage.getReceiverGroup().getId(), entityModel);
        else
            messageTemplate.convertAndSendToUser(newMessage.getReceiverUser().getId().toString(), "/queue/messages", entityModel);
    }


    @DeleteMapping("/users/{senderId}/messages/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long messageId) {
        log.info("Deleting message with ID: {}", messageId);
        Message existingMessage = repository.findById(messageId)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Message", "id", messageId));
        if (!Objects.equals(existingMessage.getSender().getId(), userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))) {
            log.error("You Cannot delete message of another user");
            throw new CustomExceptionHandling.ResourceNotFoundException("Message", "ID", messageId);
        }
        repository.delete(existingMessage);
        log.info("Message deleted: {}", existingMessage);
        return ResponseEntity.noContent().build();
    }
}
