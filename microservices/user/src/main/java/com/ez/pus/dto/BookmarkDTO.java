package com.ez.pus.dto;


import com.ez.pus.entity.Bookmark;
import com.ez.pus.user.ShortUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDTO {
    private ShortUserDTO user;
    private ShortPostDTO post;
    @Builder.Default
    private LocalDateTime savedAt = LocalDateTime.now();

    public static BookmarkDTO fromBookmark(Bookmark bookmark) {
        return BookmarkDTO.builder()
                .user(ShortUserDTO.builder()
                        .image(bookmark.getUser().getImage())
                        .id(bookmark.getUser().getId())
                        .username(bookmark.getUser().getUsername())
                        .fullname(bookmark.getUser().getFullname())
                        .build())
                .post(ShortPostDTO.fromPost(bookmark.getPost()))
                .savedAt(bookmark.getSavedAt())
                .build();
    }
}
