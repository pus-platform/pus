package com.ez.pus.enums;

import com.ez.pus.enums.course.BethlehemUniversityCourse;
import com.ez.pus.enums.major.BethlehemUniversityMajor;
import lombok.Getter;

@Getter
@SuppressWarnings("ALL")
public enum University {
    ISLAMIC_UNIVERSITY_OF_GAZA("Islamic University of Gaza", "iugaza.edu.ps", null, null),
    ALQUDS_OPEN_UNIVERSITY("Al-Quds Open University", "qou.edu", null, null),
    ARAB_AMERICAN_UNIVERSITY("Arab American University", "aaup.edu", null, null),
    BETHLEHEM_UNIVERSITY("Bethlehem University", "bethlehem.edu", BethlehemUniversityMajor.values(), BethlehemUniversityCourse.values()),
    ANNAJAH_NATIONAL_UNIVERSITY("An-Najah National University", "najah.edu", null, null),
    UNIVERSITY_OF_PALESTINE("University of Palestine", "up.edu.ps", null, null),
    ALQUDS_UNIVERSITY("Al-Quds University", "alquds.edu", null, null),
    ALAQSA_UNIVERSITY("Al-Aqsa University", "alaqsa.edu.ps", null, null),
    PALESTINE_POLYTECHNIC_UNIVERSITY("Palestine Polytechnic University", "ppu.edu", null, null),
    BIRZEIT_UNIVERSITY("Birzeit University", "birzeit.edu", null, null),
    HEBRON_UNIVERSITY("Hebron University", "hebron.edu", null, null),
    ALAZHAR_UNIVERSITY("Al-Azhar University", "Azhar.edu.eg", null, null),
    ALISTIQLAL_UNIVERSITY("Al-Istiqlal University", "pass.ps", null, null),
    PALESTINE_TECHNICAL_UNIVERSITY_KADOORIE("Palestine Technical University - Kadoorie", "ptuk.edu.ps", null, null),
    PALESTINE_TECHNICAL_COLLEGE("Palestine Technical College", "ptca.edu.ps", null, null),
    PALESTINE_AHLIYA_UNIVERSITY("Palestine Ahliya University", "paluniv.edu.ps", null, null),
    DAR_ALKALIMA_UNIVERSITY("Dar Al-Kalima University", "daralkalima.edu.ps", null, null);

    private final String name;
    private final String domain;
    private final Course<?>[] courses;
    private final Major<?>[] majors;

    University(String name, String domain, Major<?>[] majors, Course<?>[] courses) {
        this.name = name;
        this.domain = domain;
        this.majors = majors;
        this.courses = courses;
    }

    public Course<?>[] getCourses() {
        return courses;
    }

    public Major<?>[] getMajors() {
        return majors;
    }
}
