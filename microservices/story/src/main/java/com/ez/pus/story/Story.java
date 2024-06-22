package com.ez.pus.story;

import com.ez.pus.enums.View;
import com.ez.pus.replyToStory.ReplyToStory;
import com.ez.pus.storyLike.StoryLike;
import com.ez.pus.viewer.Viewer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "story")
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private String imageUrl;

    @NotNull(message = "Please specify the story's view")
    @Enumerated(EnumType.STRING)
    private View view;

    @JsonBackReference("user-story")
    @NotNull(message = "Please specify the story's creator")
    @Column(name = "user_id")
    private Long userId;

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
