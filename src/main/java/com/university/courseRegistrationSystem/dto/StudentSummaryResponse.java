package com.university.courseRegistrationSystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class StudentSummaryResponse {

    private Long id;
    private String name;
    private String email;

}