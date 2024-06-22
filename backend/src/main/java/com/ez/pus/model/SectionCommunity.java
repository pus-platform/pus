package com.ez.pus.model;

import com.ez.pus.enums.Course;
import com.ez.pus.service.CourseConverter;
import com.ez.pus.service.CourseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity(name = "section_community")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionCommunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "section_group_id")
    GroupChat sectionGroup;

    @Override
    public String toString() {
        return this.getCourse().toString() + '_' + this.getDivision();
    }
}
