package com.leave_backend.leave.services;

import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<ResponseDTO> createUser(User user) {
        String sql = """
            INSERT INTO users (user_password, employee_enabled, role_id, employee_id)
            VALUES (:password, TRUE, :role_id, :employee_id)
        """;
        Map<String, Object> params = new HashMap<>();
        params.put("password", passwordEncoder.encode(user.getUserPassword()));
        params.put("role_id", user.getRoleId());
        params.put("employee_id", user.getEmployeeId());
        int queryResult = namedParameterJdbcTemplate.update(sql, params);
        if (queryResult > 0) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Create user successfully")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Cannot create user")
                    .build());
        }
    }
}
