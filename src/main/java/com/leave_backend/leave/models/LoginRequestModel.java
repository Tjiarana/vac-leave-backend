package com.leave_backend.leave.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class LoginRequestModel {
    @NotNull(message = "Employee ID is required")
    @Positive(message = "Employee ID must be positive")
    private int employeeId;
    @NotBlank(message = "Password is required")
    private String password;
}
