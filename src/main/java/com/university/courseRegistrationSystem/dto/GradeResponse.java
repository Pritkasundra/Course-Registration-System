package com.university.courseRegistrationSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class GradeResponse {
    private Long studentId;
    private Long gradeId;
    private Long courseId;
    private String studentName;
    private String courseName;
    private String courseCode;
    private String letterGrade;
    private BigDecimal gradePoints;
    private String semester;
    private LocalDateTime gradedAt;
}
