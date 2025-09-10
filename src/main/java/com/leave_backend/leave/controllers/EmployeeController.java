package com.leave_backend.leave.controllers;

import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAuthority('ROLE_MANAGER') OR hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/employee")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAll() { return employeeService.getAllEmployee(); }

    @GetMapping(value = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getEmployeeDTOById(@PathVariable String id) {
        return employeeService.getEmployeeDTOById(id);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addEmployee(@Valid @RequestBody Employee employee) {
        return employeeService.insertEmployee(employee);
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateEmployee(@Valid @RequestBody Employee employee) {
        return employeeService.updateEmployee(employee);
    }
}
