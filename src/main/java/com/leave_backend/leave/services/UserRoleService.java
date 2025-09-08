package com.leave_backend.leave.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserRoleService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void createUserRole(int employeeId, List<String> roles) {
        String sqlInsertUserRole = """
            INSERT INTO user_roles (user_id, role_id)
            VALUES (:user_id, :role_id)
        """;

        String sqlFindUserId = """
            SELECT user_id FROM users WHERE employee_id = :employee_id        
        """;

        String sqlFindRoleId = """
            SELECT role_id FROM roles WHERE role_name = :role_name
        """;
        int userId = namedParameterJdbcTemplate.queryForObject(sqlFindUserId, Map.of("employee_id", employeeId), Integer.class);
        List<Integer> rolesId = new ArrayList<>();

        for (String role : roles) {
            int roleId = namedParameterJdbcTemplate.queryForObject(sqlFindRoleId, Map.of("role_name", role), Integer.class);
            rolesId.add(roleId);
        }

        for (int roleId : rolesId) {
            namedParameterJdbcTemplate.update(sqlInsertUserRole, Map.of("user_id", userId, "role_id", roleId));
        }
    }
}
