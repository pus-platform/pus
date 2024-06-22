package com.ez.pus.comment;

import com.ez.pus.commentLike.CommentLikeDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private Long user;
    private Long post;
    @NotNull(message = "Please add the comment content")
    private String content;

    @Builder.Default
    private LocalDateTime commentedAt = LocalDateTime.now();

    private Long repliedComment;

    private List<CommentLikeDTO> reactions;
    private List<Comment> replies;

    public static CommentDTO fromComment(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .user(comment.getUser())
                .post(comment.getPost().getId())
                .content(comment.getContent())
                .commentedAt(comment.getCommentedAt())
                .repliedComment(comment.getRepliedComment() == null ? null : comment.getRepliedComment().getId())
                .reactions(comment.getReactions().stream().map(CommentLikeDTO::fromCommentLike).toList())
                .build();
    }
}
