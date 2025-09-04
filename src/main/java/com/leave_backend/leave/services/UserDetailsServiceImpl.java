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
            SELECT u.user_id, u.employee_id, u.user_password, u.employee_enabled, r.role_name
            FROM users u
            JOIN roles r ON u.role_id = r.role_id
            WHERE u.employee_id = :employee_id
            LIMIT 1 
            """;

    @Override
    public UserDetails loadUserByUsername(String username) {
        Long employeeId = Long.valueOf(username);
        Map<String, Object> params = new HashMap<>();
        params.put("employee_id", employeeId);

        return namedParameterJdbcTemplate.query(sqlFindAuth, params, rs -> {
            if (!rs.next()) {
                throw new UsernameNotFoundException("Employee not found");
            }

//            int userId = rs.getInt("user_id");
            int empId = rs.getInt("employee_id");
            String pwdHash = rs.getString("user_password");
            boolean enabled = rs.getBoolean("employee_enabled");
            String roleName = rs.getString("role_name");

            var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));

            return new org.springframework.security.core.userdetails.User(
                    String.valueOf(empId),
                    pwdHash,
                    enabled,
                    true, true, true,
                    authorities
            );
        });
    }
}
