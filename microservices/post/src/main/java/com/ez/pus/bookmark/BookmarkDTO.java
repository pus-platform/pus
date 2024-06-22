
package com.ez.pus.bookmark;

import com.ez.pus.post.ShortPostDTO;
import lombok.*;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkDTO {
    private Long user;
    private ShortPostDTO post;
    @Builder.Default
    private LocalDateTime savedAt = LocalDateTime.now();

    public static BookmarkDTO fromBookmark(Bookmark bookmark) {
        return BookmarkDTO.builder()
                .user(bookmark.getUserId())
                .post(ShortPostDTO.fromPost(bookmark.getPost()))
                .savedAt(bookmark.getSavedAt())
                .build();
    }
}
