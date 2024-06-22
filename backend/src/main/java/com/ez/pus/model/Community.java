package com.ez.pus.model;

import com.ez.pus.enums.University;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "community")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Community {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "image_url")
    private String imageUrl;

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose a university")
    private University name;

    @ToString.Exclude
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @JsonManagedReference("community-post")
    @Builder.Default
    private List<Post> posts = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @JsonManagedReference("community-story")
    @Builder.Default
    private List<Story> stories = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @JsonManagedReference("community-user")
    @Builder.Default
    private List<User> members = new ArrayList<>();
}
