package com.ez.pus.dto;

import com.ez.pus.enums.Reaction;
import com.ez.pus.model.PostLike;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
                        .isActive(postLike.getUser().getIsActive().name())
                        .fullname(postLike.getUser().getFullname())
                        .build())
                .post(ShortPostDTO.fromPost(postLike.getPost()))
                .likedAt(postLike.getLikedAt())
                .reaction(postLike.getReaction())
                .build();
    }
}
