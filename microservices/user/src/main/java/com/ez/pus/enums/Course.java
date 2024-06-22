package com.ez.pus.enums;

import com.ez.pus.exception.CustomExceptionHandling;
import org.reflections.Reflections;

import java.util.Set;

/**
 * This interface represents a Course.
 * It provides a method for getting the name of a course and a static method for
 * getting a Course instance from a course name.
 * The Course interface is implemented by enums that represent specific courses.
 *
 * @param <E> The type of enum that implements Major
 */
@SuppressWarnings("rawtypes")
public interface Course<E extends Enum<E> & Course<E>> {

    /**
     * Returns a Course instance that corresponds to the given course name.
     * The method uses reflection to find all enums that implement the Course
     * interface and checks if any of their constants match the given course name.
     *
     * @param courseName The name of the course.
     * @return The Course instance that corresponds to the given course name.
     * @throws CustomExceptionHandling.ResourceNotFoundException If no Course
     *                                                           instance is found
     *                                                           that corresponds to
     *                                                           the given course
     *                                                           name.
     */
    static Course<?> valueOfCourse(String courseName) {
        Reflections reflections = new Reflections("com.ez.pus.enums.course");
        Set<Class<? extends Course>> classes = reflections.getSubTypesOf(Course.class);
        if (courseName == null) return null;
        for (Class<? extends Course> course : classes) {
            if (course.isEnum()) {
                for (Object obj : course.getEnumConstants()) {
                    Course<?> constant = (Course<?>) obj;
                    if (constant.toString().equals(courseName))
                        return constant;
                }
            }
        }
        throw new CustomExceptionHandling.ResourceNotFoundException("Course", "name", courseName);
    }

    /**
     * Returns the name of the course.
     *
     * @return The name of the course.
     */
    String getName();
}
