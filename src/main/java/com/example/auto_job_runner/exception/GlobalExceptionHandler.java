package com.example.auto_job_runner.exception;

import com.example.auto_job_runner.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handlevalidationException(MethodArgumentNotValidException ex){
        String errorMessage=ex.getBindingResult().getFieldError().getDefaultMessage();

        ApiResponse<Object> response = new ApiResponse<>(false, errorMessage , null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex){
        ApiResponse<Object> response = new ApiResponse<>(false, ex.getMessage(), null);
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex){
        ApiResponse<Object> response = new ApiResponse<>(false,"Something went wrong", null);
        return new ResponseEntity<>(response,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFound(ResourceNotFoundException ex){
        ApiResponse<Object> response = new ApiResponse<>(false,ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

   @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException ex){
        ApiResponse<Object> response = new ApiResponse<>(false,ex.getMessage(),null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
   }


}
