package com.ez.pus.dto;

import com.ez.pus.model.SharePost;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharePostDTO {
    private Long id;

    private ShortPostDTO post;
    
    private ShortUserDTO user;

    @Builder.Default
    private LocalDateTime sharedAt = LocalDateTime.now();

    public static SharePostDTO fromSharePost(SharePost sharePost) {
        return SharePostDTO.builder()
                .id(sharePost.getId())
                .post(ShortPostDTO.fromPost(sharePost.getPost()))
                .user(
                        ShortUserDTO.builder()
                                .image(sharePost.getUser().getImage())
                                .id(sharePost.getUser().getId())
                                .isActive(sharePost.getUser().getIsActive().name())
                                .fullname(sharePost.getUser().getFullname())
                                .username(sharePost.getUser().getUsername())
                                .build())
                .sharedAt(sharePost.getSharedAt())
                .build();
    }
}
