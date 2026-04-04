package com.university.courseRegistrationSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateSeatMatrixRequest {
    public String code;
    public int newTotalSeats;
}
