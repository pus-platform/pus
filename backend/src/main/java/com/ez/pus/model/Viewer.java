package com.ez.pus.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "viewer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Viewer.ViewerId.class)
@Builder
public class Viewer {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ViewerId implements Serializable {
        private Long user;
        private Long story;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "Please specify viewer's user")
    @JsonBackReference("user-view")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "story_id")
    @NotNull(message = "Please specify viewer's story")
    @JsonBackReference("story-view")
    private Story story;

    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();
}
