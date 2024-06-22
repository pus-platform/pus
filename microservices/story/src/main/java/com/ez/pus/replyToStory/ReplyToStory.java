package com.ez.pus.replyToStory;

import com.ez.pus.story.Story;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "reply_to_story")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReplyToStory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference("user-story_reply")
    @NotNull(message = "Please specify the story reply's user")
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "reply_content")
    @NotNull(message = "Please specify the story reply's content")
    private String replyContent;

    @ManyToOne
    @JsonBackReference("story-reply")
    @JoinColumn(name = "story_id")
    @NotNull(message = "Please specify the story reply's story")
    private Story story;

    @Column(name = "replied_at")
    @Builder.Default
    private LocalDateTime repliedAt = LocalDateTime.now();


}
