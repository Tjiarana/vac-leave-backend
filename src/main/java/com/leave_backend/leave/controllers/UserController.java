package com.leave_backend.leave.controllers;

import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.User;
import com.leave_backend.leave.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/user")
@RestController
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ResponseDTO> createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }
}
