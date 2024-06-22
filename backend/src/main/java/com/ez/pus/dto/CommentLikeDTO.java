package com.ez.pus.dto;

import com.ez.pus.enums.Reaction;
import com.ez.pus.model.CommentLike;
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
    private ShortUserDTO user;

    private Long comment;
    
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose a reaction")
    private Reaction reaction;

    public static CommentLikeDTO fromCommentLike(CommentLike commentLike) {
        return CommentLikeDTO.builder()
                .user(ShortUserDTO.builder()
                        .image(commentLike.getUser().getImage())
                        .id(commentLike.getUser().getId())
                        .username(commentLike.getUser().getUsername())
                        .isActive(commentLike.getUser().getIsActive().name())
                        .fullname(commentLike.getUser().getFullname())
                        .build())
                .comment(commentLike.getComment().getId())
                .likedAt(commentLike.getLikedAt())
                .reaction(commentLike.getReaction())
                .build();
    }
}
