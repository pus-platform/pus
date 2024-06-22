package com.ez.pus.service;

import com.ez.pus.dto.NotificationDTO;
import com.ez.pus.enums.NotificationType;
import com.ez.pus.exception.CustomExceptionHandling;
import com.ez.pus.model.*;
import com.ez.pus.repository.NotificationRepository;
import com.ez.pus.repository.UserRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@SuppressWarnings("incomplete-switch")
@Service
@Setter
public class NotificationService {
    private final NotificationRepository repository;
    private final UserRepository userRepository;
    private User trigger;
    private User consumer;
    private String username;
    private Message message;
    private GroupMember member;
    private PostLike postLike;
    private SharePost share;
    private CommentLike commentLike;
    private ReplyToStory storyReply;
    private Comment comment;
    private StoryLike storyLike;
    private Follower follower;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public void sendNotification(NotificationType type) {
        setUser(type);
        consumer = userRepository.findById(consumer.getId()).orElseThrow(() -> new CustomExceptionHandling.ResourceNotFoundException("User", "ID", consumer.getId()));
        Notification notification = repository.saveAndFlush(Notification.builder()
                .content(setContent(type))
                .notificationType(type)
                .user(consumer)
                .notifiedAt(LocalDateTime.now())
                .build());
        sendRealTimeNotification(NotificationDTO.fromNotification(notification));
    }

    private void setUser(NotificationType type) {
        switch (type) {
            case FOLLOW_REQUEST -> consumer = follower.getFollowed();
            case POST_SHARE -> consumer = share.getPost().getUser();
            case POST_LIKE -> consumer = postLike.getPost().getUser();
            case STORY_LIKE -> consumer = storyLike.getStory().getUser();
            case STORY_REPLY -> consumer = storyReply.getStory().getUser();
            case COMMENT -> consumer = comment.getPost().getUser();
            case COMMENT_LIKE -> consumer = commentLike.getComment().getUser();
            case MESSAGE -> consumer = message.getReceiverUser();
            case GROUP_MESSAGE -> consumer = member.getUser();
        }
        switch (type) {
            case FOLLOW_REQUEST -> trigger = follower.getFollower();
            case POST_SHARE -> trigger = share.getUser();
            case POST_LIKE -> trigger = postLike.getUser();
            case STORY_LIKE -> trigger = storyLike.getUser();
            case STORY_REPLY -> trigger = storyReply.getUser();
            case COMMENT_REPLY, COMMENT -> trigger = comment.getUser();
            case COMMENT_LIKE -> trigger = commentLike.getUser();
            case MESSAGE, GROUP_MESSAGE -> trigger = message.getSender();
        }
        username = trigger.getUsername();
    }

    private String setContent(NotificationType type) {
        return switch (type) {
            case FOLLOW_REQUEST -> username + " followed you";
            case POST_SHARE -> username + " shared your post";
            case POST_LIKE -> username + " liked your post";
            case STORY_LIKE -> username + " liked your story";
            case STORY_REPLY -> username + " replied to your story";
            case COMMENT_REPLY -> username + " replied to your comment";
            case COMMENT -> username + " commented on your post";
            case COMMENT_LIKE -> username + " liked your comment";
            case MESSAGE -> username + " sent you a message";
            case GROUP_MESSAGE -> message.getReceiverGroup().getName() + ": " + username + " sent a message";
        };
    }

    public void sendRealTimeNotification(NotificationDTO notificationDTO) {
        messagingTemplate.convertAndSendToUser(notificationDTO.getUser().getUsername(), "/queue/notifications", notificationDTO);
    }
}
