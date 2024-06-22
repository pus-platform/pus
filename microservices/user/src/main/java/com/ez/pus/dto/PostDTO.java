package com.ez.pus.dto;


import com.ez.pus.enums.University;
import com.ez.pus.enums.View;
import com.ez.pus.entity.Post;
import com.ez.pus.user.ShortUserDTO;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDTO {
    private Long id;

    private ShortUserDTO user;

    @Enumerated(EnumType.STRING)
    private University community;

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
    private List<SharePostDTO> shares;

    public static PostDTO fromPost(Post post) {
        return PostDTO.builder()
                .id(post.getId())
                .user(ShortUserDTO.builder()
                        .image(post.getUser().getImage())
                        .id(post.getUser().getId())
                        .username(post.getUser().getUsername())
                        .fullname(post.getUser().getFullname())
                        .build())
                .createdAt(post.getCreatedAt())
                .location(post.getLocation())
                .caption(post.getCaption())
                .view(post.getView())
                .comments(post.getComments().stream().map(CommentDTO::fromComment).toList())
                .likes(post.getLikes().stream().map(PostLikeDTO::fromPostLike).toList())
                .shares(post.getSharedByUsers().stream().map(SharePostDTO::fromSharePost).toList())
                .build();
    }
}
