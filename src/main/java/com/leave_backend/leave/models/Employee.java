package com.leave_backend.leave.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    @Positive
    @NotNull(message = "Employee id is required")
    private Long id;

    @NotBlank(message = "Firstname is required")
    @Size(max = 20, message = "Firstname must not exceed 20 characters")
    private String employeeFirstname;

    @NotBlank(message = "Lastname is required")
    @Size(max = 20, message = "Lastname must not exceed 20 characters")
    private String employeeLastname;

    @NotNull(message = "Gender is required")
    private Gender employeeGender;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @Size(max = 45, message = "Email must not exceed 45 characters")
    private String employeeEmail;

    @Size(max = 10, message = "Phone number must not exceed 10 digits")
    @NotNull(message = "Email is required")
    private String employeePhone;

    @Positive
    private Long reportTo;

    @NotNull(message = "Position ID is required")
    @Positive(message = "Position ID must be positive")
    private int positionId;
}
