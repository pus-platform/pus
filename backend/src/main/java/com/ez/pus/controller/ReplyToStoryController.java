package com.ez.pus.controller;

import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.dto.ReplyToStoryDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.Message;
import com.ez.pus.model.Notification;
import com.ez.pus.model.ReplyToStory;
import com.ez.pus.model.Story;
import com.ez.pus.model.User;
import com.ez.pus.repository.MessageRepository;
import com.ez.pus.repository.ReplyToStoryRepository;
import com.ez.pus.repository.StoryRepository;
import com.ez.pus.repository.UserRepository;
import com.ez.pus.security.service.EncryptionUtil;
import com.ez.pus.security.jwt.AuthTokenFilter;
import com.ez.pus.service.NotificationService;

import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@CrossOrigin(origins = {"http://localhost:5173", "http://localhsot:3000"})
@RestController
@RequestMapping("/stories")
@RequiredArgsConstructor
public class ReplyToStoryController {

    private static final Logger logger = LoggerFactory.getLogger(ReplyToStoryController.class);

    @Component
    static class ReplyToStoryModelAssembler
            implements RepresentationModelAssembler<ReplyToStory, EntityModel<ReplyToStoryDTO>> {
        @Override
        public @NonNull EntityModel<ReplyToStoryDTO> toModel(@NonNull ReplyToStory reply) {
            return EntityModel.of(ReplyToStoryDTO.fromReplyToStory(reply),
                    linkTo(methodOn(ReplyToStoryController.class).getReply(reply.getId())).withSelfRel(),
                    linkTo(methodOn(ReplyToStoryController.class).getRepliesToStory(reply.getStory().getId()))
                            .withRel("storyReplies"),
                    linkTo(methodOn(StoryController.class).getStoryById(reply.getStory().getId())).withRel("story"),
                    linkTo(methodOn(UserController.class).one(reply.getUser().getId())).withRel("user"));
        }
    }

    private final ReplyToStoryRepository replyToStoryRepository;
    private final NotificationService notificationService;
    private final MessageRepository messageRepository;
    private final StoryRepository storyRepository;
    private final UserRepository userRepository;
    private final ReplyToStoryModelAssembler assembler;
    private final SimpMessagingTemplate messageTemp;

    @MessageMapping("/story")
    public void addReplyToStory(@Valid @RequestBody ReplyToStoryDTO replyDTO) {
        Long storyId = replyDTO.getStory();
        logger.trace("Attempting to add a reply to story with ID: {}", storyId);
        User sender = userRepository.findById(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt()))
                .orElseThrow(() -> {
                    logger.error("Failed to find user");
                    return new CustomExceptionHandling.ResourceNotFoundException("User", "Username", AuthTokenFilter.getCurrentUserByJwt());
                });
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> {
                    logger.error("Failed to find story with ID: {}", storyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
                });
        User receiver = story.getUser();

        Message savedMessage = new Message();
        savedMessage.setMessageContent(replyDTO.getReplyContent());
        String encryptedContent = EncryptionUtil.encrypt(savedMessage.getMessageContent());
        savedMessage.setMessageContent(encryptedContent);
        savedMessage.setSender(userRepository.findById(userRepository.findIdByUsername(AuthTokenFilter.getCurrentUserByJwt())).get());
        savedMessage.setReceiverUser(storyRepository.findById(storyId).orElseThrow(() -> {
            logger.error("Failed to find story with ID: {}", storyId);
            return new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId);
        }).getUser());
        savedMessage.setIsRead(false);
        savedMessage.setSentAt(replyDTO.getRepliedAt());
        messageRepository.save(savedMessage);
        ReplyToStory reply = new ReplyToStory();
        reply.setMessage(savedMessage);
        reply.setReplyContent(encryptedContent);
        reply.setUser(sender);
        reply.setStory(story);
        ReplyToStory savedReply = replyToStoryRepository.save(reply);

        notificationService.setStoryReply(savedReply);
        notificationService.sendNotification(NotificationType.STORY_REPLY);

        // Real-time notification
        notificationService.sendRealTimeNotification(NotificationDTO.fromNotification(
                Notification.builder()
                        .notificationType(NotificationType.STORY_REPLY)
                        .content(sender.getUsername() + " replied to your story")
                        .user(receiver)
                        .notifiedAt(LocalDateTime.now())
                        .build()
        ));

        logger.info("Successfully added a reply to story with ID: {}", storyId);
        messageTemp.convertAndSendToUser(savedReply.getUser().getId().toString(), "/queue/messages", savedReply);
    }

    @GetMapping("/{storyId}/replies")
    public CollectionModel<EntityModel<ReplyToStoryDTO>> getRepliesToStory(@PathVariable Long storyId) {
        logger.trace("Fetching replies for story with ID: {}", storyId);
        if (!Objects.equals(storyRepository.findById(storyId)
                .orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("Story", "ID", storyId))
                .getUser().getUsername(), AuthTokenFilter.getCurrentUserByJwt())) {
            logger.error("You cannot view replies of a story that's not yours");
            throw new CustomExceptionHandling.InvalidArgumentException("You cannot view replies of a story that's not yours");
        }
        List<EntityModel<ReplyToStoryDTO>> replies = replyToStoryRepository.findByStoryId(storyId).stream()
                .map(reply -> {
                    String content = reply.getReplyContent();
                    try {
                        Base64.getDecoder().decode(content);
                        reply.setReplyContent(EncryptionUtil.decrypt(content));
                    } catch (IllegalArgumentException e) {
                        reply.setReplyContent(content);
                    }
                    return assembler.toModel(reply);
                })
                .collect(Collectors.toList());
        if (replies.isEmpty()) {
            logger.warn("No replies found for story with ID: {}", storyId);
            return CollectionModel.of(List.of());
        }
        logger.info("Fetched {} replies for story with ID: {}", replies.size(), storyId);
        return CollectionModel.of(replies);
    }

    @GetMapping("/{storyId}/replies/{replyId}")
    public EntityModel<ReplyToStoryDTO> getReply(@PathVariable Long replyId) {
        logger.trace("Fetching reply with ID: {}", replyId);
        ReplyToStory reply = replyToStoryRepository.findById(replyId)
                .orElseThrow(() -> {
                    logger.error("Failed to find reply with ID: {}", replyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Reply", "ID", replyId);
                });
        if (!Objects.equals(AuthTokenFilter.getCurrentUserByJwt(), reply.getUser().getUsername()) &&
                !Objects.equals(AuthTokenFilter.getCurrentUserByJwt(), reply.getStory().getUser().getUsername())) {
            logger.error("cannot view story reply of another user");
            throw new CustomExceptionHandling.InvalidArgumentException("cannot view story reply of another user");
        }
        String content = reply.getReplyContent();
        try {
            Base64.getDecoder().decode(content);
            reply.setReplyContent(EncryptionUtil.decrypt(content));
        } catch (IllegalArgumentException e) {
            reply.setReplyContent(content);
        }
        logger.info("Successfully fetched reply with ID: {}", replyId);
        return new ReplyToStoryModelAssembler().toModel(reply);
    }

    @PutMapping("/{storyId}/replies/{replyId}")
    public ResponseEntity<EntityModel<ReplyToStoryDTO>> updateReply(@PathVariable Long replyId,
                                                                    @Valid @RequestBody ReplyToStoryDTO newReplyDTO) {
        logger.trace("Updating reply with ID: {}", replyId);
        ReplyToStory updatedReply = replyToStoryRepository.findById(replyId)
                .map(reply -> {
                    if (!Objects.equals(AuthTokenFilter.getCurrentUserByJwt(), reply.getUser().getUsername())) {
                        logger.error("cannot edit story reply of another user");
                        throw new CustomExceptionHandling.InvalidArgumentException("cannot edit story reply of another user");
                    }
                    reply.getMessage().setMessageContent(newReplyDTO.getReplyContent());
                    return replyToStoryRepository.save(reply);
                })
                .orElseThrow(() -> {
                    logger.error("Failed to find reply with ID: {}", replyId);
                    return new CustomExceptionHandling.ResourceNotFoundException("Reply", "ID", replyId);
                });
        logger.info("Successfully updated reply with ID: {}", replyId);
        return ResponseEntity.created(linkTo(methodOn(ReplyToStoryController.class)
                        .getReply(updatedReply.getId())).withSelfRel().toUri())
                .body(new ReplyToStoryModelAssembler().toModel(updatedReply));
    }

    @DeleteMapping("/{storyId}/replies/{replyId}")
    public ResponseEntity<?> deleteReply(@PathVariable Long replyId) {
        logger.trace("Attempting to delete reply with ID: {}", replyId);
        replyToStoryRepository.findById(replyId)
                .ifPresentOrElse(reply -> {
                    if (!Objects.equals(AuthTokenFilter.getCurrentUserByJwt(), reply.getUser().getUsername())) {
                        logger.error("cannot delete story reply of another user");
                        throw new CustomExceptionHandling.InvalidArgumentException("cannot delete story reply of another user");
                    }
                    replyToStoryRepository.delete(reply);
                    logger.info("Successfully deleted reply with ID: {}", replyId);
                }, () -> {
                    logger.error("Failed to find reply with ID: {} for deletion", replyId);
                    throw new CustomExceptionHandling.ResourceNotFoundException("Reply", "ID", replyId);
                });
        return ResponseEntity.noContent().build();
    }
}
