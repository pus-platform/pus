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

    private String location;

    private String caption;

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
                        .fullname(post.getUser().getFullname())
                        .build())
                .createdAt(post.getCreatedAt())
                .location(post.getLocation())
                .caption(post.getCaption())
                .view(post.getView())
                .build();
    }

}
