package com.ez.pus.user;

import com.ez.pus.enums.Gender;
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

    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(nullable = false)
    @Builder.Default
    private String image = "defaultImagePath.jpg";

    @Builder.Default
    @ToString.Exclude
    private List<Integer> following = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    private List<Integer> followers = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    private List<Integer> posts = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    private List<Integer> bookmarks = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    private List<Integer> postLikes = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    private List<Integer> stories = new ArrayList<>();
}
