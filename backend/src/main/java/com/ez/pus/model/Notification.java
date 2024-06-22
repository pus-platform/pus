package com.ez.pus.model;

import com.ez.pus.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "notification")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    @NotNull(message = "Please specify the notification type")
    private NotificationType notificationType;

    @NotNull(message = "Please add notification content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-notification")
    private User user;

    @Column(name = "notified_at")
    @Builder.Default
    private LocalDateTime notifiedAt = LocalDateTime.now();
}
