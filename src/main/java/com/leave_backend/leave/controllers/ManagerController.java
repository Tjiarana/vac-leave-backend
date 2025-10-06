package com.leave_backend.leave.controllers;

import com.leave_backend.leave.services.ManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@PreAuthorize("hasAuthority('ROLE_MANAGER') OR hasAuthority('ROLE_ADMIN')")
@RequiredArgsConstructor
@RequestMapping("/api/manager")
@RestController
public class ManagerController {
    private final ManagerService managerService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAllManager() {
        return managerService.getAllManager();
    }

    @GetMapping(value = "/other", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getOtherManager(String exceptId) {
        return managerService.getOtherManager(exceptId);
    }
}
