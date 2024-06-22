package com.ez.pus.dto;

import com.ez.pus.enums.Reaction;
import com.ez.pus.model.StoryLike;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryLikeDTO {
    
    private ShortUserDTO user;
    
    private Long story;
    
    @NotNull(message = "Please add the reaction of the story like")
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    public static StoryLikeDTO fromStoryLike(StoryLike storyLike) {
        return StoryLikeDTO.builder()
                .user(ShortUserDTO.builder()
                        .image(storyLike.getUser().getImage())
                        .id(storyLike.getUser().getId())
                        .username(storyLike.getUser().getUsername())
                        .isActive(storyLike.getUser().getIsActive().name())
                        .fullname(storyLike.getUser().getFullname())
                        .build())
                .story(storyLike.getStory().getId())
                .reaction(storyLike.getReaction())
                .likedAt(storyLike.getLikedAt())
                .build();
    }
}
