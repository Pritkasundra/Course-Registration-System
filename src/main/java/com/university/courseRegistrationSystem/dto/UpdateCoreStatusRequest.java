package com.university.courseRegistrationSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateCoreStatusRequest {
    public String code;
    public boolean isCoreFlag;
}
