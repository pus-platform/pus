package com.ez.pus.dto;

import com.ez.pus.enums.NotificationType;
import com.ez.pus.model.Notification;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;

    @NotNull(message = "Please add notification content")
    @Size(min = 1, max = 500, message = "Notification content must be between 1 and 500 characters")
    private String content;

    private ShortUserDTO user;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please specify the notification type")
    private NotificationType notificationType;

    @Builder.Default
    private LocalDateTime notifiedAt = LocalDateTime.now();

    public static NotificationDTO fromNotification(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .content(notification.getContent())
                .user(ShortUserDTO.builder()
                        .image(notification.getUser().getImage())
                        .id(notification.getUser().getId())
                        .username(notification.getUser().getUsername())
                        .isActive(notification.getUser().getIsActive().name())
                        .fullname(notification.getUser().getFullname())
                        .build())
                .notificationType(notification.getNotificationType())
                .notifiedAt(notification.getNotifiedAt())
                .build();
    }
}
