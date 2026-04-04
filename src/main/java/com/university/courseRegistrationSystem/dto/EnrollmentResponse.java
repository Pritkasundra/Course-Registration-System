package com.university.courseRegistrationSystem.dto;

import com.university.courseRegistrationSystem.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class EnrollmentResponse {

    private String courseName;
    private String courseCode;
    private int creditHours;
    private boolean isCoreFlag;
    private String professorEmail;

}