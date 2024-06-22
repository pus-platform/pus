package com.ez.pus.entity;


import com.ez.pus.enums.View;
import com.ez.pus.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    private Long id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private String location;
    private String caption;
    private String imageUrl;
    private View view;

    @JsonBackReference("user-post")
    private Long user_id;

    @Transient
    private User user;

    @ToString.Exclude
    @Builder.Default
    @JsonManagedReference("post-bookmark")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @JsonManagedReference("post-comment")
    private List<Comment> comments = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @JsonManagedReference("post-like")
    private List<PostLike> likes = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @JsonManagedReference("post-share")
    private List<SharePost> sharedByUsers = new ArrayList<>();
}
