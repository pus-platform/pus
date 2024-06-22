package com.ez.pus.entity;

import com.ez.pus.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class SharePost {
    private Long id;
    private String location;
    private String caption;

    @JsonBackReference("user-share")
    private Long user_id;

    @Transient
    private User user;

    @JsonBackReference("post-share")
    private Post post;

    @Builder.Default
    LocalDateTime sharedAt = LocalDateTime.now();
}
