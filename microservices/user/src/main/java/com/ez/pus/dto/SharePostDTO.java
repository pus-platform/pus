package com.ez.pus.dto;

import com.ez.pus.entity.SharePost;
import com.ez.pus.user.ShortUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SharePostDTO {
    private Long id;
    private ShortPostDTO post;
    private ShortUserDTO user;
    private String location;
    private String caption;
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
                                .fullname(sharePost.getUser().getFullname())
                                .username(sharePost.getUser().getUsername())
                                .build())
                .location(sharePost.getLocation())
                .caption(sharePost.getCaption())
                .sharedAt(sharePost.getSharedAt())
                .build();
    }
}
