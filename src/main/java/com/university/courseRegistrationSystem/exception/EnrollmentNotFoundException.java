package com.university.courseRegistrationSystem.exception;

public class EnrollmentNotFoundException extends RuntimeException{
    public EnrollmentNotFoundException(String message){
        super(message);
    }
}
