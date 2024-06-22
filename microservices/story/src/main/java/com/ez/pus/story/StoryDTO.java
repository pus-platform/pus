package com.ez.pus.story;

import com.ez.pus.enums.View;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryDTO {
    private Long id;
    private Long user;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull(message = "Please specify the story's view")
    @Enumerated(EnumType.STRING)
    private View view;

    private String imageUrl;

    public static StoryDTO fromStory(Story story) {
        return StoryDTO.builder()
                .id(story.getId())
                .user(story.getUserId())
                .createdAt(story.getCreatedAt())
                .view(story.getView())
                .build();
    }
}