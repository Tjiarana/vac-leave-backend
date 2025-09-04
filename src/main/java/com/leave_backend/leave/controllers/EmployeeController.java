package com.leave_backend.leave.controllers;

import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.services.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAuthority('ROLE_MANAGER')")
@RequiredArgsConstructor
@RequestMapping("/api/employee")
@RestController
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<ResponseDTO> getAll() {
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
