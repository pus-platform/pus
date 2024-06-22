package com.ez.pus.commentLike;

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
public class CommentLikeDTO {
    private Long user;
    private Long comment;
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose a reaction")
    private Reaction reaction;

    public static CommentLikeDTO fromCommentLike(CommentLike commentLike) {
        return CommentLikeDTO.builder()
                .user(commentLike.getUserId())
                .comment(commentLike.getComment().getId())
                .likedAt(commentLike.getLikedAt())
                .reaction(commentLike.getReaction())
                .build();
    }
}
