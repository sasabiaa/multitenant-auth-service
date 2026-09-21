package org.project.multitenantauthservice.controller;

import org.project.multitenantauthservice.exception.BadRequestException;
import org.project.multitenantauthservice.entity.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(RuntimeException exception){
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .message(exception.getMessage())
                .data(null)
                .status("error")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex){
        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .message(ex.getMessage())
                .data(null)
                .status("error")
                .timestamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
