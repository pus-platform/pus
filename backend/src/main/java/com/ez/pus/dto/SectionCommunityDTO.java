package com.ez.pus.dto;

import com.ez.pus.enums.Course;
import com.ez.pus.model.SectionCommunity;
import com.ez.pus.service.CourseConverter;
import com.ez.pus.service.CourseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.Convert;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionCommunityDTO {
    private Long id;

    @Convert(converter = CourseConverter.class)
    @NotNull(message = "Please choose the section's course")
    @JsonDeserialize(using = CourseDeserializer.class)
    private Course<?> course;

    @NotNull(message = "Please choose the section's division")
    private Long division;

    @Min(value = 2024)
    @NotNull(message = "Please choose the section's year")
    private Long year;

    @Min(value = 1)
    @Max(value = 2)
    @NotNull(message = "Please choose the section's semester")
    private Long semester;

    @ToString.Exclude
    private Long sectionGroup;

    public static SectionCommunityDTO fromSectionCommunity(SectionCommunity sectionCommunity) {
        return SectionCommunityDTO.builder()
                .id(sectionCommunity.getId())
                .course(sectionCommunity.getCourse())
                .division(sectionCommunity.getDivision())
                .year(sectionCommunity.getYear())
                .semester(sectionCommunity.getSemester())
                .sectionGroup(sectionCommunity.getSectionGroup().getId())
                .build();
    }

    @Override
    public String toString() {
        return this.getCourse().toString() + '_' + this.getDivision();
    }
}
