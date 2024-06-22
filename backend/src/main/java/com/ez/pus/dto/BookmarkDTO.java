package com.ez.pus.dto;

import com.ez.pus.model.Bookmark;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDTO {
    private ShortUserDTO user;
    private PostDTO post;
    @Builder.Default
    private LocalDateTime savedAt = LocalDateTime.now();

    public static BookmarkDTO fromBookmark(Bookmark bookmark) {
        return BookmarkDTO.builder()
                .user(ShortUserDTO.builder()
                        .image(bookmark.getUser().getImage())
                        .id(bookmark.getUser().getId())
                        .username(bookmark.getUser().getUsername())
                        .isActive(bookmark.getUser().getIsActive().name())
                        .fullname(bookmark.getUser().getFullname())
                        .build())
                .post(PostDTO.fromPost(bookmark.getPost()))
                .savedAt(bookmark.getSavedAt())
                .build();
    }
}
