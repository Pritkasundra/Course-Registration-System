package com.university.courseRegistrationSystem.dto;

import com.university.courseRegistrationSystem.model.LetterGrade;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRequest {
    private Long StudentId;
    private Long CourseId;
    private LetterGrade letterGrade;
    private String semester;
}
