package com.ez.pus.model;

import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "group_chat")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Please add the group name")
    private String name;

    @ToString.Exclude
    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    @JsonManagedReference("group_chat-group_member")
    @Builder.Default
    private List<GroupMember> members = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "receiverGroup", cascade = CascadeType.ALL)
    @JsonManagedReference("group-message")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
