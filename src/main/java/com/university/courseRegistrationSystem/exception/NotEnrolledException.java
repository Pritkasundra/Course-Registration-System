package com.university.courseRegistrationSystem.exception;

public class NotEnrolledException extends RuntimeException{
    public NotEnrolledException(String message){
        super(message);
    }
}
