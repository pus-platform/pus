package com.ez.pus.dto;

import com.ez.pus.model.Viewer;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewerDTO {
    
    private ShortUserDTO user;
    
    private Long story;
    
    @Builder.Default
    private LocalDateTime viewedAt = LocalDateTime.now();

    public static ViewerDTO fromViewer(Viewer viewer) {
        return ViewerDTO.builder()
                .user(ShortUserDTO.builder()
                        .image(viewer.getUser().getImage())
                        .id(viewer.getUser().getId())
                        .username(viewer.getUser().getUsername())
                        .isActive(viewer.getUser().getIsActive().name())
                        .fullname(viewer.getUser().getFullname())
                        .build())
                .story(viewer.getStory().getId())
                .viewedAt(viewer.getViewedAt())
                .build();
    }
}
