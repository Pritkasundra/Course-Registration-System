package com.university.courseRegistrationSystem.dto;

import com.university.courseRegistrationSystem.model.LetterGrade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class GradeRequest {

    private String courseCode;
    private Long studentId;
    private String semester;
    private LetterGrade letterGrade;

}
