package com.university.courseRegistrationSystem.exception;

public class GradeAlreadyExistsException extends RuntimeException{
    public GradeAlreadyExistsException(String message){
        super(message);
    }
}
