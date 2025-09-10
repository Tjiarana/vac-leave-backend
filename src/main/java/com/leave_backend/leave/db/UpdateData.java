package com.leave_backend.leave.db;

import com.leave_backend.leave.models.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int updateEmployee(String id, Map<String, Object> employeeInfo) {
        String sql = """
                UPDATE employees
                SET employee_firstname = :employee_firstname, employee_lastname = :employee_lastname,
                employee_gender = :employee_gender, employee_email = :employee_email,
                employee_phone = :employee_phone, report_to = :report_to, position_id = :position_id
                WHERE employee_id = :employee_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource(employeeInfo)
                .addValue("employee_id", id);
        int affectedRow = namedParameterJdbcTemplate.update(sql, parameters);
        log.info("Updated Employee affected [{}] row.", affectedRow);
        return affectedRow;
    }
}
