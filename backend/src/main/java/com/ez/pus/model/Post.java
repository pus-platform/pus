package com.ez.pus.model;

import com.ez.pus.enums.View;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "post")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private String caption;
    @Column(name = "image_url")
    private String imageUrl;

    @NotNull(message = "Please specify post's view")
    @Enumerated(EnumType.STRING)
    private View view;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("user-post")
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_id")
    @JsonBackReference("community-post")
    private Community community;

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference("post-bookmark")
    @Builder.Default
    private List<Bookmark> bookmarks = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference("post-comment")
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference("post-like")
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    @JsonManagedReference("post-share")
    @Builder.Default
    private List<SharePost> sharedByUsers = new ArrayList<>();
}
