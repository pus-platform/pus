package com.ez.pus.postLike;

import com.ez.pus.enums.Reaction;
import com.ez.pus.post.ShortPostDTO;
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
    private Long user;
    private ShortPostDTO post;
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @NotNull(message = "Please specify the post like reaction")
    @Enumerated(EnumType.STRING)
    private Reaction reaction;

    public static PostLikeDTO fromPostLike(PostLike postLike) {
        return PostLikeDTO.builder()
                .user(postLike.getUserId())
                .post(ShortPostDTO.fromPost(postLike.getPost()))
                .likedAt(postLike.getLikedAt())
                .reaction(postLike.getReaction())
                .build();
    }
}
