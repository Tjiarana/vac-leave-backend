package com.leave_backend.leave.controllers;

import com.leave_backend.leave.models.User;
import com.leave_backend.leave.models.UserRequestModel;
import com.leave_backend.leave.services.RoleService;
import com.leave_backend.leave.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("hasAuthority('ROLE_MANAGER') OR hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;
    private final RoleService roleService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> insertUser(@Valid @RequestBody UserRequestModel user) {
        return userService.insertUser(user);
    }

    @PutMapping(consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> updateUser(@Valid @RequestBody UserRequestModel user) {
        return userService.updateUser(user);
    }

    @DeleteMapping(value = "/{employeeId}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> deleteUser(@PathVariable String employeeId) { return userService.deleteUser(employeeId); }

    @GetMapping(value = "/{employeeId}/role", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getUserRolesByEmployeeId(@PathVariable String employeeId) {
        return roleService.getUserRolesByEmployeeId(employeeId);
    }
}
