package com.ez.pus.commentLike;

import com.ez.pus.comment.Comment;
import com.ez.pus.enums.Reaction;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity(name = "comment_like")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CommentLike.CommentLikeId.class)
@Builder
public class CommentLike {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CommentLikeId implements Serializable {
        private Long comment;
        private Long userId;
    }

    @Id
    @JsonBackReference("user-comment_like")
    @NotNull(message = "Please specify the comment like's user")
    @Column(name = "user_id")
    private Long userId;


    @Id
    @ManyToOne
    @JsonBackReference("comment-like")
    @JoinColumn(name = "comment_id")
    @NotNull(message = "Please specify the comment like's comment")
    private Comment comment;

    @Column(name = "liked_at")
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose a reaction")
    private Reaction reaction;
}
