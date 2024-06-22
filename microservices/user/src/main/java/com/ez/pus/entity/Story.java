package com.ez.pus.entity;


import com.ez.pus.enums.View;
import com.ez.pus.user.User;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Story {
    private Long id;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    private String imageUrl;
    private View view;
    private User user;
}
