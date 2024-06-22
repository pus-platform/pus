package com.ez.pus.post;

import com.ez.pus.comment.CommentDTO;
// import com.ez.pus.enums.University;
import com.ez.pus.enums.View;
import com.ez.pus.postLike.PostLikeDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;

    private Long user;

    // @Enumerated(EnumType.STRING)
    // private University community;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String location;

    private String caption;
    private String imageUrl;

    @NotNull(message = "Please specify post's view")
    @Enumerated(EnumType.STRING)
    private View view;

    private List<PostLikeDTO> likes;
    private List<CommentDTO> comments;

    public static PostDTO fromPost(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .user(post.getUser())
                .createdAt(post.getCreatedAt())
                .location(post.getLocation())
                .caption(post.getCaption())
                .view(post.getView())
                .comments(post.getComments().stream().map(CommentDTO::fromComment).toList())
                .likes(post.getLikes().stream().map(PostLikeDTO::fromPostLike).toList())
//                .file(FileDTO.fromFile(post.getFile().getFirst()))
                .build();
    }
}
