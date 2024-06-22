package com.ez.pus.model;

import java.time.LocalDateTime;

import com.ez.pus.enums.MessageReceiver;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "message")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Please add the message content")
    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "sent_at")
    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    @Column(name = "is_read")
    @NotNull(message = "Please specify if the message is read or not (boolean)")
    private Boolean isRead;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    @JsonBackReference("receiver-message")
    private User receiverUser;

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonBackReference("group-message")
    private GroupChat receiverGroup;

    @NotNull(message = "Please specify the receiver of the message")
    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type")
    private MessageReceiver receiverType;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonBackReference("sender-message")
    private User sender;

    public User getReceiverUser() {
        return receiverType == MessageReceiver.USER ? receiverUser : null;
    }

    public GroupChat getReceiverGroup() {
        return receiverType == MessageReceiver.GROUP_CHAT ? receiverGroup : null;
    }

    public void setMReceiver() {
        if (receiverType == MessageReceiver.USER) {
            this.receiverGroup = null;
        } else {
            this.receiverUser = null;
        }
    }

}
