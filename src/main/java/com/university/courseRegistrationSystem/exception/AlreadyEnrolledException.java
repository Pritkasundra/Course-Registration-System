package com.university.courseRegistrationSystem.exception;

public class AlreadyEnrolledException extends RuntimeException{
    public AlreadyEnrolledException(String message){
        super(message);
    }
}
