package com.leave_backend.leave.controllers;

import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.services.EmployeeService;
import io.jsonwebtoken.Jwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@Slf4j
@PreAuthorize("hasAuthority('ROLE_MANAGER') OR hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/employee")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAllEmployee() { return employeeService.getAllEmployee(); }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getEmployeeById(@PathVariable String id) { return employeeService.getEmployeeById(id); }

    @GetMapping(value = "/dto", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAllEmployeeDTO() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String employeeId = auth.getName();
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        boolean isAdmin = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        if (isAdmin) {
            return employeeService.getAllEmployeeDTO();
        }
        return employeeService.getAllEmployeeDTO(employeeId);
    }

    @GetMapping(value = "/dto/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getEmployeeDTOById(@PathVariable String id) {
        return employeeService.getEmployeeDTOById(id);
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> insertEmployee(@Valid @RequestBody Employee employee) {
        return employeeService.insertEmployee(employee);
    }

    @PutMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateEmployee(@Valid @RequestBody Employee employee) {
        return employeeService.updateEmployee(employee);
    }
}
