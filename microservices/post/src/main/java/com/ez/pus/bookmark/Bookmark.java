package com.ez.pus.bookmark;

import com.ez.pus.post.Post;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "bookmark")
@IdClass(Bookmark.BookmarkId.class)
public class Bookmark {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkId implements Serializable {
        private Long userId;
        private Long post;
    }

    @Id
    @JsonBackReference("user-bookmark")
    @Column(name = "user_id")
    @NotNull(message = "Please specify bookmark's user")
    private Long userId;

    @Id
    @ManyToOne
    @JsonBackReference("post-bookmark")
    @JoinColumn(name = "post_id")
    @NotNull(message = "Please specify bookmark's user")
    private Post post;

    @Column(name = "saved_at")
    private LocalDateTime savedAt = LocalDateTime.now();
}
