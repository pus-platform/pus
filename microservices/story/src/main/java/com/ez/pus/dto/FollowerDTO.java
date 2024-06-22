package com.ez.pus.dto;

import com.ez.pus.entity.Follower;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
                        .fullname(follower.getFollower().getFullname())
                        .build())
                .followed(ShortUserDTO.builder()
                        .image(follower.getFollowed().getImage())
                        .id(follower.getFollowed().getId())
                        .username(follower.getFollowed().getUsername())
                        .fullname(follower.getFollowed().getFullname())
                        .build())
                .followedAt(follower.getFollowedAt())
                .build();
    }
}
