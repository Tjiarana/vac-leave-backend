package com.leave_backend.leave.controllers;

import com.leave_backend.leave.dto.employee.EmployeeDTO;
import com.leave_backend.leave.exception.dto.Response;
import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.models.Position;
import com.leave_backend.leave.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/employee")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<Response> getAll() {
        return employeeService.getAllEmployee();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<EmployeeDTO> getEmployeeDTOById(@PathVariable Long id) {
//        EmployeeDTO employeeDTO = employeeService.getEmployeeDTOById(id);
//        return ResponseEntity.ok(employeeDTO);
//    }
//
//    @PostMapping
//    public ResponseEntity<Integer> addEmployee(@Valid @RequestBody Employee employee) {
//        employeeService.addEmployee(employee);
//        URI location = URI.create("/api/employee/" + employee.getId());
//        return ResponseEntity.created(location).build();
//    }
}
