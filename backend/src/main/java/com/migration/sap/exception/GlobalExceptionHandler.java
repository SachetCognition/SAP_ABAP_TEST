package com.migration.sap.exception;

import com.migration.sap.dto.ApiErrorResponse;
import com.migration.sap.dto.ApiMessage;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle InvalidInputException -> HTTP 400.
     * Replaces ABAP RAISE invalid_input.
     */
    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidInput(InvalidInputException ex) {
        ApiMessage msg = new ApiMessage("E", "ZMSG", "001", ex.getMessage());
        ApiErrorResponse response = new ApiErrorResponse(0, Collections.singletonList(msg));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handle DataAccessException -> HTTP 500.
     * Replaces cx_sy_open_sql_db catch at FUGR lines 104-107.
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiErrorResponse> handleDataAccess(DataAccessException ex) {
        ApiMessage msg = new ApiMessage("E", "ZMSG", "003", "An internal database error occurred. Please contact support.");
        ApiErrorResponse response = new ApiErrorResponse(0, Collections.singletonList(msg));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handle validation exceptions -> HTTP 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation error");
        ApiMessage msg = new ApiMessage("E", "ZMSG", "004", message);
        ApiErrorResponse response = new ApiErrorResponse(0, Collections.singletonList(msg));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
