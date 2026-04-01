package com.university.courseRegistrationSystem.exception;

public class ProfessorNotAuthorizedException extends RuntimeException{
    public ProfessorNotAuthorizedException(String message){
        super(message);
    }
}
