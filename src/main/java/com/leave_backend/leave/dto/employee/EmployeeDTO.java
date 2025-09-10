package com.leave_backend.leave.dto.employee;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.leave_backend.leave.models.Position;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDTO {
    private String id;
    private String name;
    private EmployeeDTO reportTo;
    private String position;
}
