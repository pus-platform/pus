package com.ez.pus.replyToStory;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyToStoryDTO {
    private Long id;

    @NotNull(message = "Please specify the story reply's content")
    private String replyContent;

    private Long user;
    private Long story;

    @Builder.Default
    private LocalDateTime repliedAt = LocalDateTime.now();

    public static ReplyToStoryDTO fromReplyToStory(ReplyToStory replyToStory) {
        return ReplyToStoryDTO.builder()
                .id(replyToStory.getId())
                .replyContent(replyToStory.getReplyContent())
                .user(replyToStory.getUserId())
                .story(replyToStory.getStory().getId())
                .repliedAt(replyToStory.getRepliedAt())
                .build();
    }
}
