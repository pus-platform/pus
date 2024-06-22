package com.ez.pus.enums.course;

import com.ez.pus.enums.Course;

/**
 * This enum represents the courses offered by Bethlehem University.
 * Each enum constant represents a specific course and implements the Course interface.
 */
@SuppressWarnings("ALL")
public enum BethlehemUniversityCourse implements Course<BethlehemUniversityCourse> {
    HIST120("Modern & Contemporary History of Palestine"),
    LIBR101("Library Skills"),
    MATH141("Calculus & Analytic Geometry I"),
    MATH142("Calculus & Analytic Geometry II"),
    MATH238("Discrete Mathematics"),
    MATH331("Probability"),
    PHIL350("Computer Ethics"),
    PHYS113("Laboratory Practice"),
    PHYS131("General Physics I"),
    PHYS132("General Physics II"),
    PSED101("Physical Education"),
    SCIE140("Biology & Chemistry Fundamentals"),
    SWER141("Introduction to Programming"),
    SWER142("Object Oriented Programming"),
    SWER212("Software Construction"),
    SWER241("Data Structures & Algorithms Analysis"),
    SWER251("Introduction to Computer Systems"),
    SWER252("Operating Systems"),
    SWER253("Computer Networks"),
    SWER311("Software Requirements Engineering"),
    SWER312("Software Testing & Quality Assurance");

    /**
     * The name of the course.
     */
    final String name;

    /**
     * Constructs a new BethlehemUniversityCourse with the given name.
     *
     * @param name The name of the course.
     */
    BethlehemUniversityCourse(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the course.
     *
     * @return The name of the course.
     */
    @Override
    public String getName() {
        return name;
    }
}
