package com.ez.pus.entity;

import com.ez.pus.enums.Reaction;
import com.ez.pus.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentLike {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentLikeId implements Serializable {
        private Long comment;
        private Long user;
    }
    private Long user_id;

    @Transient
    private User user;

    private Comment comment;
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();
    private Reaction reaction;
}
