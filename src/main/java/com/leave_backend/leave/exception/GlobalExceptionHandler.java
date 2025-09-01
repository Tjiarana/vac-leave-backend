package com.leave_backend.leave.exception;

import com.leave_backend.leave.exception.custom.EmployeeNotFoundException;
import com.leave_backend.leave.exception.custom.InvalidFieldDataException;
import com.leave_backend.leave.exception.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(EmployeeNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ResponseEntity<Response> handleEmployeeNotFound(EmployeeNotFoundException ex) {
//        Response errorResponse = Response.builder()
//                .status(HttpStatus.NOT_FOUND.value())
//                .error("Employee Not Found")
//                .message(ex.getMessage())
//                .timestamp(LocalDateTime.now())
//                .build();
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
//    }
//
//    @ExceptionHandler(InvalidFieldDataException.class)
//    public ResponseEntity<Map<String, String>> handleInvalidEmployeeData(InvalidFieldDataException ex) {
//        Map<String, String> error = new HashMap<>();
//        error.put("status", "Error");
//        error.put("message", ex.getMessage());
//        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
//    }
//
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
