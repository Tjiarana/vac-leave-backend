package com.leave_backend.leave.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int deleteUserRoles(String userId) {
        String sql = """
                DELETE FROM user_roles
                WHERE user_id = :user_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", userId);
        int affectedRow = namedParameterJdbcTemplate.update(sql, parameters);
        log.info("Delete User Roles affected [{}] row.", affectedRow);
        return affectedRow;
    }

    public int deleteUser(String userId) {
        String sql = """
                DELETE FROM users
                WHERE user_id = :user_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_id", userId);
        int affectedRow = namedParameterJdbcTemplate.update(sql, parameters);
        log.info("Delete User affected [{}] row.", affectedRow);
        return affectedRow;
    }

    public int deleteEmployee(String employeeId) {
        String sql = """
                DELETE FROM employees
                WHERE employee_id = :employee_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", employeeId);
        int affectedRow = namedParameterJdbcTemplate.update(sql, parameters);
        log.info("Delete Employee affected [{}] row.", affectedRow);
        return affectedRow;
    }
}
