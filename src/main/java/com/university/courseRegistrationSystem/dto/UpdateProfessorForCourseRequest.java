package com.university.courseRegistrationSystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfessorForCourseRequest {

    public String code;
    public String professorEmail;

}
