package com.ez.pus.dto;

import com.ez.pus.enums.MessageReceiver;
import com.ez.pus.model.Message;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;

    @NotNull(message = "Please add the message content")
    @Size(min = 1, max = 500, message = "Message content must be between 1 and 500 characters")
    private String messageContent;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    @NotNull(message = "Please specify if the message is read or not (boolean)")
    private Boolean isRead;

    private ShortUserDTO receiverUser;

    private Long receiverGroup;

    private MessageReceiver receiverType;

    private ShortUserDTO sender;

    public static MessageDTO fromMessage(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .messageContent(message.getMessageContent())
                .sentAt(message.getSentAt())
                .isRead(message.getIsRead())
                .receiverUser(message.getReceiverUser() == null ? null :  ShortUserDTO.builder()
                        .image(message.getReceiverUser().getImage())
                        .id(message.getReceiverUser().getId())
                        .username(message.getReceiverUser().getUsername())
                        .isActive(message.getReceiverUser().getIsActive().name())
                        .fullname(message.getReceiverUser().getFullname())
                        .build())
                .receiverGroup(message.getReceiverGroup()== null ? null : message.getReceiverGroup().getId())
                .receiverType(message.getReceiverType())
                .sender(ShortUserDTO.builder()
                        .image(message.getSender().getImage())
                        .id(message.getSender().getId())
                        .username(message.getSender().getUsername())
                        .isActive(message.getSender().getIsActive().name())
                        .fullname(message.getSender().getFullname())
                        .build())
//                .file(FileDTO.fromFile(message.getFile().getFirst()))
                .build();
    }
}
