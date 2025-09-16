package com.leave_backend.leave.services;

import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.LoginRequestModel;
import com.leave_backend.leave.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final QueryData queryData;

    public ResponseEntity<Object> login(LoginRequestModel request) {
        try {
            if (queryData.queryUserIdByEmployeeId(request.getEmployeeId()) == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                        .status("error")
                        .code("EMP_NOT_FOUND")
                        .message("Employee not found with id: " + request.getEmployeeId())
                        .build());
            }
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(String.valueOf(request.getEmployeeId()), request.getPassword())
            );
            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user.getUsername(), user.getAuthorities());
            Date expiration = jwtUtil.getExpirationDateFromToken(token);
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Login Successfully")
                    .data(Map.of("token", token, "roles", user.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .distinct()
                            .toList(),
                            "token_expiration", expiration))
                    .build());
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseDTO.builder()
                            .status("error")
                            .code("EMP_INVALID_PASS")
                            .message("Login failed, invalid password")
                            .build()
            );
        }
    }
}
