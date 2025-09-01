package com.leave_backend.leave.exception.custom;

public class EmployeeAlreadyExistException extends RuntimeException {
    public EmployeeAlreadyExistException(String message) {
        super(message);
    }
}
