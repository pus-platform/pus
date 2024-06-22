package com.ez.pus.postLike;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ez.pus.enums.Reaction;
import com.ez.pus.post.Post;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "post_like")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PostLike.PostLikeId.class)
@Builder
public class PostLike {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PostLikeId implements Serializable {
        private Long post;
        private Long userId;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "post_id")
    @NotNull(message = "Please specify post like's post")
    @JsonBackReference("post-like")
    private Post post;

    @Id
    @NotNull(message = "Please specify post like's user")
    @JsonBackReference("user-post_like")
    private Long userId;


    @Column(name = "liked_at")
    @Builder.Default
    private LocalDateTime likedAt = LocalDateTime.now();

    @NotNull(message = "Please specify the post like reaction")
    @Enumerated(EnumType.STRING)
    private Reaction reaction;
}
