package com.ez.pus.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "follower")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(Follower.FollowerId.class)
public class Follower {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FollowerId implements Serializable {
        private Long follower;
        private Long followed;
    }
    @Id
    @ManyToOne
    @JsonBackReference("user-follower")
    @JoinColumn(name = "follower_id")
    @NotNull(message = "Please specify the follower user")
    private User follower;

    @Id
    @ManyToOne
    @JsonBackReference("user-followed")
    @JoinColumn(name = "followed_id")
    @NotNull(message = "Please specify the followed user")
    private User followed;

    @Column(name = "followed_at")
    @Builder.Default
    private LocalDateTime followedAt = LocalDateTime.now();
}
