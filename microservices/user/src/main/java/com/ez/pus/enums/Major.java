package com.ez.pus.enums;

import com.ez.pus.exception.CustomExceptionHandling;
import org.reflections.Reflections;

import java.util.Set;

/**
 * This interface represents a Major.
 * It provides a method for getting the name of a major and a static method for
 * getting a Major instance from a major name.
 * The Major interface is implemented by enums that represent specific majors.
 *
 * @param <E> The type of enum that implements Major
 */
@SuppressWarnings("rawtypes")
public interface Major<E extends Enum<E> & Major<E>> {

    /**
     * Returns the name of the major.
     *
     * @return The name of the major.
     */
    String getName();

    /**
     * Returns a Major instance that corresponds to the given major name.
     * The method uses reflection to find all enums that implement the Major
     * interface and checks if any of their constants match the given major name.
     *
     * @param majorName The name of the major.
     * @return The Major instance that corresponds to the given major name.
     * @throws CustomExceptionHandling.ResourceNotFoundException If no Major
     *                                                           instance is found
     *                                                           that corresponds to
     *                                                           the given major
     *                                                           name.
     */
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
