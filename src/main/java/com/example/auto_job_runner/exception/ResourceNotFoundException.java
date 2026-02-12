package com.example.auto_job_runner.exception;

import org.springframework.http.ResponseEntity;

public class ResourceNotFoundException extends RuntimeException{
    public ResourceNotFoundException(String message){
        super(message);
    }
}
