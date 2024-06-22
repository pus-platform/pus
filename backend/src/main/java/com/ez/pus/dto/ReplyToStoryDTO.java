package com.ez.pus.dto;

import com.ez.pus.model.ReplyToStory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyToStoryDTO {
    private Long id;
    
    @NotNull(message = "Please specify the story reply's content")
    @Size(min = 1, max = 500, message = "Story reply content must be between 1 and 500 characters")
    private String replyContent;
    
    private ShortUserDTO user;
    private Long story;

    @Builder.Default
    private LocalDateTime repliedAt = LocalDateTime.now();

    public static ReplyToStoryDTO fromReplyToStory(ReplyToStory replyToStory) {
        return ReplyToStoryDTO.builder()
                .id(replyToStory.getId())
                .replyContent(replyToStory.getReplyContent())
                .user(ShortUserDTO.builder()
                        .image(replyToStory.getUser().getImage())
                        .id(replyToStory.getUser().getId())
                        .username(replyToStory.getUser().getUsername())
                        .isActive(replyToStory.getUser().getIsActive().name())
                        .fullname(replyToStory.getUser().getFullname())
                        .build())
                .story(replyToStory.getStory().getId())
                .repliedAt(replyToStory.getRepliedAt())
                .build();
    }
}
