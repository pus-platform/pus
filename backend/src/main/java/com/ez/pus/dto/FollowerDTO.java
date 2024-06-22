package com.ez.pus.dto;

import com.ez.pus.model.Follower;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FollowerDTO {
    private ShortUserDTO follower;
    private ShortUserDTO followed;
    @Builder.Default
    private LocalDateTime followedAt = LocalDateTime.now();
    public static FollowerDTO fromFollower(Follower follower) {
        return FollowerDTO.builder()
                .follower(ShortUserDTO.builder()
                        .image(follower.getFollower().getImage())
                        .id(follower.getFollower().getId())
                        .username(follower.getFollower().getUsername())
                        .isActive(follower.getFollower().getIsActive().name())
                        .fullname(follower.getFollower().getFullname())
                        .build())
                .followed(ShortUserDTO.builder()
                        .image(follower.getFollowed().getImage())
                        .id(follower.getFollowed().getId())
                        .username(follower.getFollowed().getUsername())
                        .isActive(follower.getFollowed().getIsActive().name())
                        .fullname(follower.getFollowed().getFullname())
                        .build())
                .followedAt(follower.getFollowedAt())
                .build();
    }
}
