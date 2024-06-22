package com.ez.pus.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ez.pus.enums.GroupRole;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "group_member")
@Data
@AllArgsConstructor
@NoArgsConstructor
@IdClass(GroupMember.GroupMemberId.class)
public class GroupMember {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupMemberId implements Serializable {
        private Long user;
        private Long group;
    }

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "Please add the user in group member")
    @JsonBackReference("user-group_member")
    private User user;

    @Id
    @ManyToOne
    @JoinColumn(name = "group_id")
    @NotNull(message = "Please add the group in group member")
    @JsonBackReference("group_chat-group_member")
    private GroupChat group;

    @NotNull(message = "Please add the group in group member")
    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Column(name = "member_since")
    private LocalDateTime memberSince = LocalDateTime.now();
}
