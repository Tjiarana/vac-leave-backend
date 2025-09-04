package com.leave_backend.leave.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int userId;
    @NotBlank(message = "Password is required")
    private String userPassword;
    private Boolean userEnabled;
    @Positive
    @NotNull(message = "Role id is required")
    private int roleId;
    @Positive
    @NotNull(message = "Employee id is required")
    private int employeeId;
}
