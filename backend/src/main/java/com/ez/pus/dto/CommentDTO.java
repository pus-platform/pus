package com.ez.pus.dto;

import com.ez.pus.model.Comment;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentDTO {
    private Long id;
    private ShortUserDTO user;
    private Long post;
    @NotNull(message = "Please add the comment content")
    @Size(min = 1, max = 500, message = "Comment content must be between 1 and 500 characters")
    private String content;

    @Builder.Default
    private LocalDateTime commentedAt = LocalDateTime.now();

    private Long repliedComment;

    private List<CommentLikeDTO> reactions;
    private List<Comment> replies;

    public static CommentDTO fromComment(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .user(ShortUserDTO.builder()
                        .image(comment.getUser().getImage())
                        .id(comment.getUser().getId())
                        .username(comment.getUser().getUsername())
                        .isActive(comment.getUser().getIsActive().name())
                        .fullname(comment.getUser().getFullname())
                        .build())
                .post(comment.getPost().getId())
                .content(comment.getContent())
                .commentedAt(comment.getCommentedAt())
                .repliedComment(comment.getRepliedComment() == null ? null : comment.getRepliedComment().getId())
                .reactions(comment.getReactions().stream().map(CommentLikeDTO::fromCommentLike).toList())
                .build();
    }
}
