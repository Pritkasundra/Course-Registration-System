package com.university.courseRegistrationSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class StudentProfileResponse {
    private Long studentId;
    private String name;
    private String email;
    private String semester;
    private int year;
    private BigDecimal cgpa;
    private int completedCredits;
    private int totalRequiredCredits;

    //how many credits still needed to graduate
    private int remainingCredits;
}