package com.internshiptracker;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(
            IllegalArgumentException exception)
    {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(), exception.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(),
                "Request body is invalid.");
        return ResponseEntity.status(status).body(error);

    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatch
            (MethodArgumentTypeMismatchException exception){
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(),
                "Invalid value for parameter: " + exception.getName());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public ResponseEntity<ApiError> duplicateUsernameException(DuplicateUsernameException exception){
        HttpStatus status = HttpStatus.CONFLICT;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(),exception.getMessage());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiError> handleInvalidCredentials(InvalidCredentialsException exception){
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        ApiError error = new ApiError(status.value(), status.getReasonPhrase(), exception.getMessage());
        return ResponseEntity.status(status).body(error);
    }
}
