package com.ez.pus.entity;

import com.ez.pus.enums.Gender;
import com.ez.pus.story.Story;
import com.ez.pus.viewer.Viewer;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String fullname;
    private LocalDate dob;
    private String email;
    private String password;
    private String bio;
    private Gender gender;

    @Builder.Default
    private String image = "defaultImagePath.jpg";

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-view")
    private List<Viewer> viewedStories = new ArrayList<>();

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference("user-story")
    private List<Story> stories = new ArrayList<>();
}
