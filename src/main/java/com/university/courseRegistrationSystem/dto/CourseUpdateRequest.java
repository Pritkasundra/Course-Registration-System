package com.university.courseRegistrationSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUpdateRequest {
    private Integer seats;
    private Long professorId;
    private Boolean isCoreFlag;
    private Integer creditHours;
}
