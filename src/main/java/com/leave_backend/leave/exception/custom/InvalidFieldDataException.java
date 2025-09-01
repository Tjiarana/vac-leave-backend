package com.leave_backend.leave.exception.custom;

public class InvalidFieldDataException extends RuntimeException {
    public InvalidFieldDataException(String message) {
        super(message);
    }
    public InvalidFieldDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
