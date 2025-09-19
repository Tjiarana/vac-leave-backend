package com.leave_backend.leave.models;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestModel {
    @Positive
    @NotBlank(message = "Employee id is required")
    private String employeeId;

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

    private String reportTo;

    @NotBlank(message = "Position ID is required")
    private String positionId;

    private String userPassword;

    @NotEmpty(message = "Role(s) is required")
    private List<String> roles;

}
