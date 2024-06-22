package com.ez.pus.model;

import com.ez.pus.enums.Gender;
import com.ez.pus.enums.Status;
import com.ez.pus.enums.University;
import com.fasterxml.jackson.annotation.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(unique = true)
    private String username;

    @NotNull(message = "Please specify the user's first name")
    @Size(min = 3, max = 50, message = "First name size should be between 3 and 50")
    private String fullname;

    @Past
    private LocalDate dob;

    @Email
    @NotBlank
    @Size(max = 50)
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "is_active")
    private Status isActive;
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(nullable = false, name = "image_url")
    @Builder.Default
    private String image = "defaultImagePath.jpg";

    private String verificationCode;
    @Column(name = "is_verified")
    private boolean isVerified;


    @ManyToOne
    @JoinColumn(name = "community_id")
//    @NotNull(message = "Please enter community")
    @JsonBackReference( "community-user")
    private Community community;

//    @Builder.Default
//    @ToString.Exclude
//    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL)
//    @JsonManagedReference("sender-message")
//    private List<Message> sentMessages = new ArrayList<>();

//    @Builder.Default
//    @ToString.Exclude
//    @OneToMany(mappedBy = "receiverUser", cascade = CascadeType.ALL)
//    @JsonManagedReference("receiver-message")
//    private List<Message> receivedMessages = new ArrayList<>();

//    @Builder.Default
//    @ToString.Exclude
//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
//    @JsonManagedReference("user-group_member")
//    private List<GroupMember> joinedGroups = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-notification")
    private List<Notification> notifications = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-story_reply")
    private List<ReplyToStory> repliesToStories = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL)
    @JsonManagedReference("user-follower")
    private List<Follower> following = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "followed", cascade = CascadeType.ALL)
    @JsonManagedReference("user-followed")
    private List<Follower> followers = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-post")
    private List<Post> posts = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-bookmark")
    private List<Bookmark> bookmarks = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-comment")
    private List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-comment_like")
    private List<CommentLike> commentLikes = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-post_like")
    private List<PostLike> postLikes = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-story")
    private List<Story> stories = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-story_like")
    private List<StoryLike> storyLikes = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-share")
    private List<SharePost> sharedPosts = new ArrayList<>();

    @JsonGetter("community")
    public University getCommunityName() {
        return (community != null) ? community.getName() : null;
    }
}
