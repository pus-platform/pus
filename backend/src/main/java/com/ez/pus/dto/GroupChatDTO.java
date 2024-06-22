package com.ez.pus.dto;

import com.ez.pus.model.GroupChat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupChatDTO {
    private Long id;

    @NotNull(message = "Please add the group name")
    @Size(min = 1, max = 50, message = "Group name must be between 1 and 50 characters")
    private String name;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private List<MessageDTO> messages;

    public static GroupChatDTO fromGroupChat(GroupChat groupChat) {
        return GroupChatDTO.builder()
                .id(groupChat.getId())
                .name(groupChat.getName())
                .messages(groupChat.getMessages().stream().map(MessageDTO::fromMessage).toList().reversed())
                .createdAt(groupChat.getCreatedAt())
                .build();
    }
}
