package com.ez.pus.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity(name = "share_post")
@Table
@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class SharePost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private String caption;

    @ManyToOne
    @JsonBackReference("user-share")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JsonBackReference("post-share")
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(name = "shared_at")
    @Builder.Default
    LocalDateTime sharedAt = LocalDateTime.now();
}
