package com.ez.pus.dto;

import com.ez.pus.enums.Gender;
import com.ez.pus.enums.Status;
import com.ez.pus.enums.University;
import com.ez.pus.model.User;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    private String username;

    @Size(min = 3, max = 50, message = "First name size should be between 3 and 50")
    private String fullname;

    @Past
    private LocalDate dob;

    @Email
    private String email;

    private String imageUrl;

    @Size(max = 120)
    private String password;

    @Enumerated(EnumType.STRING)
    private Status isActive;

    private String bio;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    private University community;

    private List<FollowerDTO> followers;
    private List<FollowerDTO> following;
    private List<StoryDTO> stories;
    private List<BookmarkDTO> bookmarks;
    private List<PostLikeDTO> likes;

    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .dob(user.getDob())
                .email(user.getEmail())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .bio(user.getBio())
                .gender(user.getGender())
                .community(user.getCommunity().getName())
                .followers(user.getFollowers().stream().map(FollowerDTO::fromFollower).toList())
                .following(user.getFollowing().stream().map(FollowerDTO::fromFollower).toList())
                .stories(user.getStories().stream().map(StoryDTO::fromStory).toList())
                .bookmarks(user.getBookmarks().stream().map(BookmarkDTO::fromBookmark).toList())
                .likes(user.getPostLikes().stream().map(PostLikeDTO::fromPostLike).toList())
                .build();
    }
}
