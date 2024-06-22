package com.ez.pus.entity;


import com.ez.pus.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bookmark {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookmarkId implements Serializable {
        private Long user;
        private Long post;
    }

    @JsonBackReference("user-bookmark")
    private Long user_id;

    @Transient
    private User user;

    @JsonBackReference("post-bookmark")
    private Post post;

    private LocalDateTime savedAt = LocalDateTime.now();
}
