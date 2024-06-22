package com.ez.pus.service;

import com.ez.pus.enums.Course;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CourseConverter implements AttributeConverter<Course<?>, String> {

    @Override
    public String convertToDatabaseColumn(Course<?> attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Course<?> convertToEntityAttribute(String dbData) {
        return Course.valueOfCourse(dbData);
    }
}
