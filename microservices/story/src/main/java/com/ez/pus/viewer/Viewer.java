package com.ez.pus.viewer;

import com.ez.pus.story.Story;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "viewer")
@IdClass(Viewer.ViewerId.class)
public class Viewer {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ViewerId implements Serializable {
        private Long userId;
        private Long story;
    }

    @Id
    @NotNull(message = "Please specify viewer's user")
    @Column(name = "user_id")
    private Long userId;

    @Id
    @ManyToOne
    @JoinColumn(name = "story_id")
    @NotNull(message = "Please specify viewer's story")
    @JsonBackReference("story-view")
    private Story story;

    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();
}