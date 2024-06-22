package com.ez.pus.model;

import com.ez.pus.enums.Major;
import com.ez.pus.service.MajorConverter;
import com.ez.pus.service.MajorDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;


@Entity(name = "major_community")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MajorCommunity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = MajorConverter.class)
    @NotNull(message = "Please choose the section's course")
    @JsonDeserialize(using = MajorDeserializer.class)
    private Major<?> major;

    @Min(value = 2010)
    @NotNull(message = "Please choose the section's year")
    private Long year;

    @ToString.Exclude
    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(name = "major_group_id")
    GroupChat majorGroup;

    @Override
    public String toString() {
        return this.major.toString() + '_' + this.year;
    }
}
