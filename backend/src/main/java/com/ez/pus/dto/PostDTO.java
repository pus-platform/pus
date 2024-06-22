package com.ez.pus.dto;

import com.ez.pus.enums.University;
import com.ez.pus.enums.View;
import com.ez.pus.model.Post;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @NotNull(message = "Please add the caption")
    @Size(min = 1, max = 500, message = "Caption must be between 1 and 500 characters")
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
                        .isActive(post.getUser().getIsActive().name())
                        .fullname(post.getUser().getFullname())
                        .build())
                .community(post.getCommunity() == null ? null : post.getCommunity().getName())
                .createdAt(post.getCreatedAt())
                .caption(post.getCaption())
                .view(post.getView())
                .comments(post.getComments().stream().map(CommentDTO::fromComment).toList())
                .likes(post.getLikes().stream().map(PostLikeDTO::fromPostLike).toList())
                .shares(post.getSharedByUsers().stream().map(SharePostDTO::fromSharePost).toList())
                .imageUrl(post.getImageUrl())
                .build();
    }
}
