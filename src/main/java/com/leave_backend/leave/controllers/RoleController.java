package com.leave_backend.leave.controllers;

import com.leave_backend.leave.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@PreAuthorize("hasAuthority('ROLE_MANAGER') OR hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/role")
@RestController
public class RoleController {
    private final RoleService roleService;

}
