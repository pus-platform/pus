package com.ez.pus.entity;

import com.ez.pus.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    private Long id;
    private User user;
    private Post post;
    private String content;

    @Builder.Default
    private LocalDateTime commentedAt = LocalDateTime.now();

    private Comment repliedComment;

    private List<CommentLike> reactions;
    private List<Comment> replies;

}
