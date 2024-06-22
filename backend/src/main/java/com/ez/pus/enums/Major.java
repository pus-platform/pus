package com.ez.pus.enums;

import com.ez.pus.exception.CustomExceptionHandling;
import org.reflections.Reflections;

import java.util.Set;

@SuppressWarnings("rawtypes")
public interface Major<E extends Enum<E> & Major<E>> {

    String getName();

    static Major<?> valueOfMajor(String majorName) {
        Reflections reflection = new Reflections("com.ez.pus.enums.major");
        if (majorName == null) return null;
        Set<Class<? extends Major>> universities = reflection.getSubTypesOf(Major.class);
        for (Class<? extends Major> uni : universities) {
            if (uni.isEnum()) {
                for (Object obj : uni.getEnumConstants()) {
                    Major<?> major = (Major<?>) obj;
                    if (major.toString().equals(majorName))
                        return major;
                }
            }
        }
        throw new CustomExceptionHandling.ResourceNotFoundException("Major", "Name", majorName);
    }
}
