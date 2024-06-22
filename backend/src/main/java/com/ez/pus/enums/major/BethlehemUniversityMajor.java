package com.ez.pus.enums.major;

import com.ez.pus.enums.Major;

public enum BethlehemUniversityMajor implements Major<BethlehemUniversityMajor> {
    BACHELOR_OF_SCIENCE_IN_TECHNOLOGY_FOR_ENVIRONMENTAL_SUSTAINABILITY("Bachelor of Science in Technology for Environmental Sustainability"),
    BACHELOR_OF_SUSTAINABLE_ENERGY_ENGINEERING("Bachelor of Sustainable Energy Engineering"),
    INTERACTIVE_MEDIA_AND_COMMUNICATION("Interactive Media and Communication"),
    BACHELOR_OF_SCIENCE_MAJOR_IN_MEDICAL_LABORATORY_SCIENCES("Bachelor of Science Major in Medical Laboratory Sciences"),
    COMPUTER_SIMULATION_IN_SCIENCES_AND_ENGINEERING("Computer Simulation in Sciences and Engineering"),
    ENGLISH_LANGUAGE_LITERATURE_AND_COMMUNICATIONS("English Language, Literature, and Communications"),
    BACHELOR_OF_SCIENCE_MAJOR_IN_NURSING("Bachelor of Science Major in Nursing"),
    BACHELOR_OF_SCIENCE_MAJOR_IN_MATHEMATICS("Bachelor of Science Major in Mathematics"),
    BACHELOR_OF_SCIENCE_MAJOR_IN_BIOLOGY("Bachelor of Science Major in Biology"),
    BACHELOR_OF_SCIENCE_MAJOR_IN_PHYSICS("Bachelor of Science Major in Physics"),
    BACHELOR_IN_SOFTWARE_ENGINEERING("Bachelor in Software Engineering");

    final String name;

    BethlehemUniversityMajor(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
