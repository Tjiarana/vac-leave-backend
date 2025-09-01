package com.leave_backend.leave.exception.custom;

public class EmployeeNotFoundException extends RuntimeException {
    public EmployeeNotFoundException(String message) { super(message); }
}
