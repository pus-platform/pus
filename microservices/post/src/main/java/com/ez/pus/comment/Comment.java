package com.ez.pus.comment;

import com.ez.pus.commentLike.CommentLike;
import com.ez.pus.post.Post;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "comment")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long user;

    @ManyToOne
    @JsonBackReference("post-comment")
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, length = 500)
    @NotNull(message = "Please add the comment content")
    private String content;

    @Column(name = "commented_at", nullable = false)
    @Builder.Default
    private LocalDateTime commentedAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "replied_comment_id")
    private Comment repliedComment;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    @JsonManagedReference("comment-like")
    @Builder.Default
    @ToString.Exclude
    private List<CommentLike> reactions = new ArrayList<>();
}
