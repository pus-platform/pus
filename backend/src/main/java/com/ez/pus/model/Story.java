package com.ez.pus.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.ez.pus.enums.View;

@Builder
@Entity(name = "story")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Column(name = "image_url")
    private String imageUrl;

    @NotNull(message = "Please specify the story's view")
    @Enumerated(EnumType.STRING)
    private View view;

    @ManyToOne
    @JsonBackReference("community-story")
    @JoinColumn(name = "community_id")
    private Community community;

    @ManyToOne
    @JsonBackReference("user-story")
    @JoinColumn(name = "user_id")
    @NotNull(message = "Please specify the story's creator")
    private User user;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    @JsonManagedReference("story-reply")
    @ToString.Exclude
    @Builder.Default
    private List<ReplyToStory> replies = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    @JsonManagedReference("story-view")
    @ToString.Exclude
    @Builder.Default
    private List<Viewer> views = new ArrayList<>();

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL)
    @JsonManagedReference("story-like")
    @ToString.Exclude
    @Builder.Default
    private List<StoryLike> likes = new ArrayList<>();
}
