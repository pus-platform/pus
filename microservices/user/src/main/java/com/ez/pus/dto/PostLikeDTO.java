package com.ez.pus.dto;


import com.ez.pus.enums.Reaction;
import com.ez.pus.entity.PostLike;
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
public class PostLikeDTO {
    private ShortUserDTO user;
    private ShortPostDTO post;
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @NotNull(message = "Please specify the post like reaction")
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    public static PostLikeDTO fromPostLike(PostLike postLike) {
        return PostLikeDTO.builder()
                .user(ShortUserDTO.builder()
                        .image(postLike.getUser().getImage())
                        .id(postLike.getUser().getId())
                        .username(postLike.getUser().getUsername())
                        .fullname(postLike.getUser().getFullname())
                        .build())
                .post(ShortPostDTO.fromPost(postLike.getPost()))
                .likedAt(postLike.getLikedAt())
                .reaction(postLike.getReaction())
                .build();
    }
}
