package com.ez.pus.dto;


import com.ez.pus.entity.User;
import com.ez.pus.enums.Gender;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    private String bio;

    @Enumerated(EnumType.STRING)
    private Gender gender;

//    private List<FollowerDTO> followers;
//    private List<FollowerDTO> following;
//    private List<StoryDTO> stories;
//    private List<BookmarkDTO> bookmarks;
//    private List<PostLikeDTO> likes;

    public static UserDTO fromUser(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .dob(user.getDob())
                .email(user.getEmail())
                .password(user.getPassword())
                .bio(user.getBio())
                .gender(user.getGender())
//                .followers(user.getFollowers().stream().map(FollowerDTO::fromFollower).toList())
//                .following(user.getFollowing().stream().map(FollowerDTO::fromFollower).toList())
//                .stories(user.getStories().stream().map(StoryDTO::fromStory).toList())
//                .bookmarks(user.getBookmarks().stream().map(BookmarkDTO::fromBookmark).toList())
//                .likes(user.getPostLikes().stream().map(PostLikeDTO::fromPostLike).toList())
                .build();
    }
}
