package com.ez.pus.dto;

import com.ez.pus.enums.GroupRole;
import com.ez.pus.model.GroupMember;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupMemberDTO {
    private ShortUserDTO user;
    private Long group;
    @NotNull(message = "Please add the group in group member")
    @Enumerated(EnumType.STRING)
    private GroupRole role;

    @Builder.Default
    private LocalDateTime memberSince = LocalDateTime.now();

    public static GroupMemberDTO fromGroupMember(GroupMember groupMember) {
        return GroupMemberDTO.builder()
                .user(ShortUserDTO.builder()
                        .image(groupMember.getUser().getImage())
                        .id(groupMember.getUser().getId())
                        .username(groupMember.getUser().getUsername())
                        .isActive(groupMember.getUser().getIsActive().name())
                        .fullname(groupMember.getUser().getFullname())
                        .build())
                .group(groupMember.getGroup().getId())
                .role(groupMember.getRole())
                .memberSince(groupMember.getMemberSince())
                .build();
    }
}
