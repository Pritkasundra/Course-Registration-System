package com.university.courseRegistrationSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateCreditHoursRequest {
    public String code;
    public int creditHours;
}
