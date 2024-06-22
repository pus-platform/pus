package com.ez.pus.entity;


import com.ez.pus.enums.Reaction;
import com.ez.pus.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
public class PostLike {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostLikeId implements Serializable {
        private Long post;
        private Long user;
    }

    @JsonBackReference("post-like")
    private Post post;

    @JsonBackReference("user-post_like")
    private Long user_id;

    @Transient
    private User user;

    @Column(name = "liked_at")
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    private Reaction reaction;
}
