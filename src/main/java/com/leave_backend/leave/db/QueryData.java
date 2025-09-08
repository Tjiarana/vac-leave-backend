package com.leave_backend.leave.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.models.Gender;
import com.leave_backend.leave.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ObjectMapper mapper = JsonMapper.builder().build();

    public List<Employee> queryAllEmployee() {
        String sql = """
                SELECT employee_id, employee_firstname, employee_lastname, employee_gender, employee_email, employee_phone, report_to, position_id
                FROM employees
                """;
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {
            Employee employee = new Employee();
            employee.setId(rs.getLong("employee_id"));
            employee.setEmployeeFirstname(rs.getString("employee_firstname"));
            employee.setEmployeeLastname(rs.getString("employee_lastname"));
            employee.setEmployeeGender(Gender.valueOf(rs.getString("employee_gender")));
            employee.setEmployeeEmail(rs.getString("employee_email"));
            employee.setEmployeePhone(rs.getString("employee_phone"));
            employee.setReportTo(rs.getObject("report_to", Long.class));
            employee.setPositionId(rs.getInt("position_id"));
            return employee;
        });
    }

    public Employee queryEmployee(Long id) {
        String sql = """
                SELECT employee_id, employee_firstname, employee_lastname, employee_gender, employee_email, employee_phone, report_to, position_id
                FROM employees
                WHERE employee_id = :employee_id
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", id);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                Employee employee = new Employee();
                employee.setId(rs.getLong("employee_id"));
                employee.setEmployeeFirstname(rs.getString("employee_firstname"));
                employee.setEmployeeLastname(rs.getString("employee_lastname"));
                employee.setEmployeeGender(Gender.valueOf(rs.getString("employee_gender")));
                employee.setEmployeeEmail(rs.getString("employee_email"));
                employee.setEmployeePhone(rs.getString("employee_phone"));
                employee.setReportTo(rs.getObject("report_to", Long.class));
                employee.setPositionId(rs.getInt("position_id"));
                return employee;
            } else {
                return null;
            }
        });
    }

    public ObjectNode queryEmployeeDTO(Long id) {
        String sql = """
                SELECT employee_id, employee_firstname, employee_lastname, report_to, position_id
                FROM employees
                WHERE employee_id = :employee_id
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", id);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                return mapper.createObjectNode()
                        .put("employeeId", rs.getLong("employee_id"))
                        .put("employeeName", rs.getString("employee_firstname") + " " + rs.getString("employee_lastname"))
                        .put("reportTo", rs.getObject("report_to", Long.class))
                        .put("positionId", rs.getInt("position_id"));
            } else {
                return null;
            }
        });
    }

    public String queryPosition(int id) {
        String sql = """
                SELECT position_name
                FROM positions
                WHERE position_id = :position_id
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("position_id", id);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                return rs.getString("position_name");
            } else {
                return null;
            }
        });
    }

    public List<Long> queryRolesIdByRolesName(List<String> rolesName) {
        String sql = """
                SELECT role_id
                FROM roles
                WHERE role_name = :role_name;
                """;
        List<Long> rolesId = new ArrayList<>();
        for (String roleName : rolesName) {
            SqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue("role_name", roleName);
            Long roleId = namedParameterJdbcTemplate.query(sql, parameters, rs -> {
                if (rs.next()) {
                    return rs.getObject("role_id", Long.class);
                } else {
                    return null;
                }
            });
            if (roleId != null) {
                rolesId.add(roleId);
            }
        }
        return rolesId;
    }

    public int queryUserIdByEmployeeId(Long employeeId) {
        String sql = """
                SELECT user_id
                FROM users
                WHERE employee_id = :employee_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("employee_id", employeeId);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                return rs.getInt("user_id");
            } else {
                return null;
            }
        });
    }
}
