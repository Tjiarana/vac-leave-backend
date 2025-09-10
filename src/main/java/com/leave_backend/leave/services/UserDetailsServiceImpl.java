package com.leave_backend.leave.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String sqlFindAuth = """
            SELECT u.user_id, u.employee_id, u.user_password, u.employee_enabled
            FROM users u
            WHERE u.employee_id = :employee_id
            LIMIT 1
            """;
    private static final String sqlFindRole = """
            SELECT r.role_name
            FROM roles r
            JOIN user_roles ur ON r.role_id = ur.role_id
            WHERE ur.user_id = :user_id
            """;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Map<String, Object> params = new HashMap<>();
        params.put("employee_id", username);

        return namedParameterJdbcTemplate.query(sqlFindAuth, params, rs -> {
            if (!rs.next()) {
                throw new UsernameNotFoundException("Employee not found");
            }

            String userId = rs.getString("user_id");
            String empId = rs.getString("employee_id");
            String pwdHash = rs.getString("user_password");
            boolean enabled = rs.getBoolean("employee_enabled");
            List<String> roles = findRolesByUserId(userId);

            List<SimpleGrantedAuthority> authorities = roles.stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();

            return new org.springframework.security.core.userdetails.User(
                    String.valueOf(empId),
                    pwdHash,
                    enabled,
                    true, true, true,
                    authorities
            );
        });
    }

    public List<String> findRolesByUserId(String userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("user_id", userId);
        return namedParameterJdbcTemplate.query(sqlFindRole, params, (rs, i) -> rs.getString("role_name"));
    }
}
