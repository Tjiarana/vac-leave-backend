package com.leave_backend.leave.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class InsertData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper mapper = JsonMapper.builder().build();

    public int insertEmployee(Map<String, Object> employeeInfo) {
        String sql = """
                INSERT INTO employees (employee_id, employee_firstname, employee_lastname, employee_gender, employee_email, employee_phone, report_to, position_id)
                VALUES (:employee_id, :employee_firstname, :employee_lastname, :employee_gender, :employee_email, :employee_phone, :report_to, :position_id)
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource(employeeInfo);
        int affectedRow = namedParameterJdbcTemplate.update(sql, parameters);
        log.info("Insert Employee affected [{}] row.", affectedRow);
        return affectedRow;
    }

    public int insertUser(Map<String, Object> userInfo) {
        String sql = """
            INSERT INTO users (user_password, employee_enabled, employee_id)
            VALUES (:user_password, TRUE, :employee_id)
        """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_password", userInfo.get("user_password"))
                .addValue("employee_id", userInfo.get("employee_id"));
        int affectedRow = namedParameterJdbcTemplate.update(sql, parameters);
        log.info("Insert User affected [{}] row.", affectedRow);
        return affectedRow;
    }

    public int insertUserRoles(String userId, List<String> rolesId) {
        String sql = """
            INSERT INTO user_roles (user_id, role_id)
            VALUES (:user_id, :role_id)
        """;
        int affectedRow = 0;
        for (String roleId : rolesId) {
            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("user_id", userId)
                    .addValue("role_id", roleId);
            affectedRow += namedParameterJdbcTemplate.update(sql, parameters);
        }
        log.info("Insert User Role affected [{}] row.", affectedRow);
        return affectedRow;
    }
}
