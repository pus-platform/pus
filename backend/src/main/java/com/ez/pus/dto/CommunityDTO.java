package com.ez.pus.dto;

import com.ez.pus.enums.University;
import com.ez.pus.model.Community;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityDTO {
    private Long id;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Please choose a university")
    private University name;
    
    private String imageUrl;


    public static CommunityDTO fromCommunity(Community community) {
        return CommunityDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .imageUrl(community.getImageUrl())
                .build();
    }
}
