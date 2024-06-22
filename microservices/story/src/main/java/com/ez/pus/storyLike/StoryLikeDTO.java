package com.ez.pus.storyLike;

import com.ez.pus.enums.Reaction;
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
    private Long user;
    private Long story;

    @NotNull(message = "Please add the reaction of the story like")
    @Enumerated(EnumType.STRING)
    private Reaction reaction;
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    public static StoryLikeDTO fromStoryLike(StoryLike storyLike) {
        return StoryLikeDTO.builder()
                .user(storyLike.getUserId())
                .story(storyLike.getStory().getId())
                .reaction(storyLike.getReaction())
                .likedAt(storyLike.getLikedAt())
                .build();
    }
}
