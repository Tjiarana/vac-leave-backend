package com.leave_backend.leave.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.models.Gender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class QueryData {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final ObjectMapper mapper = JsonMapper.builder().build();

    public List<Employee> queryAllEmployee() {
        String sql = """
                SELECT * FROM employees
                """;
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {
            Employee employee = new Employee();
            employee.setId(rs.getString("employee_id"));
            employee.setEmployeeFirstname(rs.getString("employee_firstname"));
            employee.setEmployeeLastname(rs.getString("employee_lastname"));
            employee.setEmployeeGender(Gender.valueOf(rs.getString("employee_gender")));
            employee.setEmployeeEmail(rs.getString("employee_email"));
            employee.setEmployeePhone(rs.getString("employee_phone"));
            employee.setReportTo(rs.getString("report_to"));
            employee.setPositionId(rs.getString("position_id"));
            return employee;
        });
    }

    public List<ObjectNode> queryAllEmployeeDTO() {
        String sql = """
                SELECT e.employee_id, e.employee_firstname, e.employee_lastname, e.report_to, p.position_name
                FROM employees as e
                JOIN positions as p on e.position_id = p.position_id
                """;
        return namedParameterJdbcTemplate.query(sql, rs -> {
            List<ObjectNode> employeeListDTO = new ArrayList<>();
            while (rs.next()) {
                ObjectNode employeeNode = mapper.createObjectNode()
                        .put("id", rs.getString("employee_id"))
                        .put("employeeName", rs.getString("employee_firstname") + " " + rs.getString("employee_lastname"))
                        .put("reportTo", rs.getString("report_to"))
                        .put("position", rs.getString("position_name"));
                employeeListDTO.add(employeeNode);
            }
            return employeeListDTO;
        });
    }

    public List<ObjectNode> queryAllEmployeeDTO(String employeeId) {
        String sql = """
                SELECT e.employee_id, e.employee_firstname, e.employee_lastname, e.report_to, p.position_name
                FROM employees as e
                JOIN positions as p on e.position_id = p.position_id
                where e.report_to = :employee_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("employee_id", employeeId);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            List<ObjectNode> employeeListDTO = new ArrayList<>();
            while (rs.next()) {
                ObjectNode employeeNode = mapper.createObjectNode()
                        .put("id", rs.getString("employee_id"))
                        .put("employeeName", rs.getString("employee_firstname") + " " + rs.getString("employee_lastname"))
                        .put("reportTo", rs.getString("report_to"))
                        .put("position", rs.getString("position_name"));
                employeeListDTO.add(employeeNode);
            }
            return employeeListDTO;
        });
    }

    public Employee queryEmployee(String id) {
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
                employee.setId(rs.getString("employee_id"));
                employee.setEmployeeFirstname(rs.getString("employee_firstname"));
                employee.setEmployeeLastname(rs.getString("employee_lastname"));
                employee.setEmployeeGender(Gender.valueOf(rs.getString("employee_gender")));
                employee.setEmployeeEmail(rs.getString("employee_email"));
                employee.setEmployeePhone(rs.getString("employee_phone"));
                employee.setReportTo(rs.getString("report_to"));
                employee.setPositionId(rs.getString("position_id"));
                return employee;
            } else {
                return null;
            }
        });
    }

    public ObjectNode queryEmployeeDTO(String id) {
        String sql = """
                SELECT e.employee_id, e.employee_firstname, e.employee_lastname, e.report_to, p.position_name
                FROM employees as e
                JOIN positions as p on e.position_id = p.position_id
                WHERE e.employee_id = :employee_id
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", id);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                return mapper.createObjectNode()
                        .put("id", rs.getLong("employee_id"))
                        .put("employeeName", rs.getString("employee_firstname") + " " + rs.getString("employee_lastname"))
                        .put("reportTo", rs.getString("report_to"))
                        .put("position", rs.getString("position_name"));
            } else {
                return null;
            }
        });
    }

    public Boolean queryExistingEmployee(String employeeId) {
        String sql = """
                SELECT 1 FROM employees
                WHERE employee_id = :employee_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", employeeId);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.query(sql, parameters, ResultSet::next));
    }

    public Map<String, List<String>> queryExistingEmployeeField(String employeeEmail, String employeePhone) {
        String sql = """
                SELECT employee_email, employee_phone FROM employees
                WHERE (employee_email = :employee_email OR employee_phone = :employee_phone)
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_email", employeeEmail)
                .addValue("employee_phone", employeePhone);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            List<String> emails = new ArrayList<>();
            List<String> phones = new ArrayList<>();
            Map<String, List<String>> queryResult = new HashMap<>();
            while (rs.next()) {
                emails.add(rs.getString("employee_email"));
                phones.add(rs.getString("employee_phone"));
            }
            queryResult.put("emails", emails);
            queryResult.put("phones", phones);
            return queryResult;
        });
    }

    public Map<String, List<String>> queryExistingOtherEmployeeField(String id, String employeeEmail, String employeePhone) {
        String sql = """
                SELECT employee_email, employee_phone FROM employees
                WHERE (employee_email = :employee_email OR employee_phone = :employee_phone)
                AND employee_id != :employee_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", id)
                .addValue("employee_email", employeeEmail)
                .addValue("employee_phone", employeePhone);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            List<String> emails = new ArrayList<>();
            List<String> phones = new ArrayList<>();
            Map<String, List<String>> queryResult = new HashMap<>();
            while (rs.next()) {
                emails.add(rs.getString("employee_email"));
                phones.add(rs.getString("employee_phone"));
            }
            queryResult.put("emails", emails);
            queryResult.put("phones", phones);
            return queryResult;
        });
    }

    public String queryPosition(String id) {
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

    public Boolean queryExistingPosition(String positionId) {
        String sql = """
                SELECT 1 FROM positions
                WHERE position_id = :position_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("position_id", positionId);
        return Boolean.TRUE.equals(namedParameterJdbcTemplate.query(sql, parameters, ResultSet::next));
    }

    public List<String> queryRolesIdByRolesName(List<String> rolesName) {
        String sql = """
                SELECT role_id
                FROM roles
                WHERE role_name IN (:roles);
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("roles", rolesName);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            List<String> rolesId = new ArrayList<>();
            while (rs.next()) {
                rolesId.add(rs.getString("role_id"));
            }
            return rolesId;
        });
    }

    public String queryUserIdByEmployeeId(String employeeId) {
        String sql = """
                SELECT user_id
                FROM users
                WHERE employee_id = :employee_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("employee_id", employeeId);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                return rs.getString("user_id");
            } else {
                return null;
            }
        });
    }
}
