package com.university.courseRegistrationSystem.dto;

import com.university.courseRegistrationSystem.model.LetterGrade;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor

public class GradeResponse {

    private Long courseId;
    private String courseName;
    private String courseCode;
    private int creditHours;
    private LetterGrade letterGrade;
    private BigDecimal gradePoints;
    private String semester;

}
