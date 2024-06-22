package com.ez.pus.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ez.pus.enums.Reaction;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "story_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(StoryLike.StoryLikeId.class)
@Builder
public class StoryLike {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StoryLikeId implements Serializable {
        private Long user;
        private Long story;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "story_id")
    @NotNull(message = "Please specify story like's story")
    @JsonBackReference("story-like")
    private Story story;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "Please specify story like's user")
    @JsonBackReference("user-story_like")
    private User user;

    @NotNull(message = "Please add the reaction of the story like")
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    @Column(name = "liked_at")
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();
}
