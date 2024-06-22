package com.ez.pus.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "bookmark")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(Bookmark.BookmarkId.class)
public class Bookmark {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkId implements Serializable {
        private Long user;
        private Long post;
    }

    @Id
    @ManyToOne
    @JsonBackReference("user-bookmark")
    @JoinColumn(name = "user_id")
    @NotNull(message = "Please specify bookmark's user")
    private User user;

    @Id
    @ManyToOne
    @JsonBackReference("post-bookmark")
    @JoinColumn(name = "post_id")
    @NotNull(message = "Please specify bookmark's user")
    private Post post;

    @Column(name = "saved_at")
    private LocalDateTime savedAt = LocalDateTime.now();
}
