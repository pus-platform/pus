package com.ez.pus.service;

import com.ez.pus.enums.Major;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class MajorDeserializer extends JsonDeserializer<Major<?>> {

    @Override
    public Major<?> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String majorName = jsonParser.getValueAsString();
        return Major.valueOfMajor(majorName);
    }
}
