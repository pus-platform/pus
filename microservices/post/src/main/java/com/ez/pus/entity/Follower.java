package com.ez.pus.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
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
    @JsonBackReference("user-follower")
    private User follower;

    @Id
    @JsonBackReference("user-followed")
    private User followed;

    @Builder.Default
    private LocalDateTime followedAt = LocalDateTime.now();
}

