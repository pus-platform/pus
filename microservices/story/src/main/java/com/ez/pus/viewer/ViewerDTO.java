package com.ez.pus.viewer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewerDTO {
    private Long user;
    private Long story;
    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();

    public static ViewerDTO fromViewer(Viewer viewer) {
        return ViewerDTO.builder()
                .user(viewer.getUserId())
                .story(viewer.getStory().getId())
                .viewedAt(viewer.getViewedAt())
                .build();
    }
}