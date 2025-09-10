package com.leave_backend.leave.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private String userId;

    @NotBlank(message = "Password is required")
    private String userPassword;

    private Boolean userEnabled;

    @NotBlank(message = "Employee id is required")
    private String employeeId;

    @NotEmpty(message = "Role(s) is required")
    private List<String> roles;
}
