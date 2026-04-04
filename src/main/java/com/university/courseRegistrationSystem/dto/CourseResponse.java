package com.university.courseRegistrationSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor

public class CourseResponse {
    private Long id;
    private String name;
    private String code;
    private int totalSeats;
    private int availableSeats;
    private int creditHours;
    private boolean isCoreFlag;
    private BigDecimal minCgpaRequired;
    private Long professorId;

}
