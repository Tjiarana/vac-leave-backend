package com.leave_backend.leave.services;

import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.LoginRequest;
import com.leave_backend.leave.models.User;
import com.leave_backend.leave.utils.JwtUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserDetailsServiceImpl userDetailsService;

    public ResponseEntity<ResponseDTO> login(LoginRequest request) {
        try {
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
                            "expiration", expiration))
                    .build());
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    ResponseDTO.builder()
                            .status("error")
                            .message("Login failed, invalid username or password")
                            .build()
            );
        }
    }
}
