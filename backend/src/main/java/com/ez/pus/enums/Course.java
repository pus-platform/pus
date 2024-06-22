package com.ez.pus.enums;

import com.ez.pus.exception.CustomExceptionHandling;
import org.reflections.Reflections;

import java.util.Set;

@SuppressWarnings("rawtypes")
public interface Course<E extends Enum<E> & Course<E>> {

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

    String getName();
}
