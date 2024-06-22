package com.ez.pus.dto;

import com.ez.pus.enums.University;
import com.ez.pus.enums.View;
import com.ez.pus.model.Post;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortPostDTO {
    Long id;

    private ShortUserDTO user;

    @Enumerated(EnumType.STRING)
    private University community;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @NotNull(message = "Please add the caption")
    @Size(min = 1, max = 500, message = "Caption must be between 1 and 500 characters")
    private String caption;

    private String image;

    @NotNull(message = "Please specify post's view")
    @Enumerated(EnumType.STRING)
    private View view;

    public static ShortPostDTO fromPost(Post post) {
        return ShortPostDTO.builder()
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
                .image(post.getImageUrl())
                .build();
    }

}
