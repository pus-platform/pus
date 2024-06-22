package com.ez.pus.dto;

import com.ez.pus.enums.University;
import com.ez.pus.enums.View;
import com.ez.pus.model.Story;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryDTO {
    private Long id;
    private ShortUserDTO user;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @NotNull(message = "Please specify the story's view")
    @Enumerated(EnumType.STRING)
    private View view;
    @Enumerated(EnumType.STRING)
    private University community;

    @NotNull(message = "Please add the image URL")
    private String imageUrl;

    private List<StoryLikeDTO> likes;
    private List<ReplyToStoryDTO> replies;
    private List<ViewerDTO> views;


    public static StoryDTO fromStory(Story story) {
        return StoryDTO.builder()
                .id(story.getId())
                .user(ShortUserDTO.builder()
                        .image(story.getUser().getImage()).id(story.getUser().getId())
                        .username(story.getUser().getUsername())
                        .isActive(story.getUser().getIsActive().name())
                        .fullname(story.getUser().getFullname())
                        .build())
                .createdAt(story.getCreatedAt())
                .view(story.getView())
                .community(story.getCommunity() == null ? null : story.getCommunity().getName())
                .imageUrl(story.getImageUrl())
                .build();
    }
}
