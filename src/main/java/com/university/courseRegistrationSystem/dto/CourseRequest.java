package com.university.courseRegistrationSystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CourseRequest {
    private String name;
    private String code;
    private int totalSeats;
    private int creditHours;
    private boolean isCoreFlag;
    private String semester;
    private Integer year;
    private BigDecimal minCgpaRequired;
    private String professorEmail;
}
