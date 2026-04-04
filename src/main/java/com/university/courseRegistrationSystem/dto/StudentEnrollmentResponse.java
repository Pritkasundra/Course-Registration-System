package com.university.courseRegistrationSystem.dto;

import com.university.courseRegistrationSystem.model.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor

public class StudentEnrollmentResponse {


    private String name;
    private String email;
    private String courseName;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;

}
