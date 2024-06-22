package com.ez.pus.dto;

import com.ez.pus.enums.Major;
import com.ez.pus.model.MajorCommunity;
import com.ez.pus.service.MajorConverter;
import com.ez.pus.service.MajorDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MajorCommunityDTO {
    private Long id;

    @Convert(converter = MajorConverter.class)
    @NotNull(message = "Please choose the section's course")
    @JsonDeserialize(using = MajorDeserializer.class)
    private Major<?> major;

    @Min(value = 2010)
    @NotNull(message = "Please choose the section's year")
    private Long year;

    private Long majorGroup;

    public static MajorCommunityDTO fromMajorCommunity(MajorCommunity majorCommunity) {
        return MajorCommunityDTO.builder()
                .major(majorCommunity.getMajor())
                .year(majorCommunity.getYear())
                .majorGroup(majorCommunity.getMajorGroup().getId())
                .build();
    }

    @Override
    public String toString() {
        return this.major.toString() + '_' + this.year;
    }
}
