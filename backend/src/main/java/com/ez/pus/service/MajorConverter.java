package com.ez.pus.service;

import com.ez.pus.enums.Major;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class MajorConverter implements AttributeConverter<Major<?>, String> {

    @Override
    public String convertToDatabaseColumn(Major<?> attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Major<?> convertToEntityAttribute(String dbData) {
        return Major.valueOfMajor(dbData);
    }
}
