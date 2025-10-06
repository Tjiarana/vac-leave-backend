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

    public ObjectNode queryEmployee(String id) {
        String sql = """
                SELECT employee_id, employee_firstname, employee_lastname, employee_gender, employee_email, employee_phone, report_to, position_id
                FROM employees
                WHERE employee_id = :employee_id
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", id);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                return mapper.createObjectNode()
                        .put("id", rs.getString("employee_id"))
                        .put("employeeFirstname", rs.getString("employee_firstname"))
                        .put("employeeLastname", rs.getString("employee_lastname"))
                        .put("employeeGender", rs.getString("employee_gender"))
                        .put("employeeEmail", rs.getString("employee_email"))
                        .put("employeePhone", rs.getString("employee_phone"))
                        .put("reportTo", rs.getString("report_to"))
                        .put("positionId", rs.getString("position_id"));
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

    public List<ObjectNode> queryAllManager() {
        String sql = """
                SELECT e.employee_id as manager_id, concat(e.employee_firstname, ' ', e.employee_lastname) as manager_name
                FROM employees as e
                JOIN users as u on e.employee_id = u.employee_id
                JOIN user_roles ur on u.user_id = ur.user_id
                JOIN roles r on ur.role_id = r.role_id
                WHERE r.role_name = 'MANAGER'
                """;
        return namedParameterJdbcTemplate.query(sql, rs -> {
            List<ObjectNode> managerList = new ArrayList<>();
            while(rs.next()) {
                ObjectNode managerNode = mapper.createObjectNode()
                        .put("managerId", rs.getString("manager_id"))
                        .put("managerName", rs.getString("manager_name"));
                managerList.add(managerNode);
            }
            return managerList;
        });
    }

    public List<ObjectNode> queryOtherManager(String exceptId) {
        String sql = """
                SELECT e.employee_id as manager_id, concat(e.employee_firstname, ' ', e.employee_lastname) as manager_name
                FROM employees as e
                JOIN users as u on e.employee_id = u.employee_id
                JOIN user_roles ur on u.user_id = ur.user_id
                JOIN roles r on ur.role_id = r.role_id
                WHERE r.role_name = 'MANAGER'
                AND e.employee_id != :employee_id
                AND (e.report_to IS NULL OR e.report_to != :employee_id)
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("employee_id", exceptId);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            List<ObjectNode> managerList = new ArrayList<>();
            while(rs.next()) {
                ObjectNode managerNode = mapper.createObjectNode()
                        .put("managerId", rs.getString("manager_id"))
                        .put("managerName", rs.getString("manager_name"));
                managerList.add(managerNode);
            }
            return managerList;
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

    public List<ObjectNode> queryAllPosition() {
        String sql = """
                SELECT position_id, position_name
                FROM positions
                """;
        return namedParameterJdbcTemplate.query(sql, rs -> {
            List<ObjectNode> positionListNode = new ArrayList<>();
            while (rs.next()) {
                ObjectNode positionNode = mapper.createObjectNode()
                    .put("positionId", rs.getString("position_id"))
                    .put("positionName", rs.getString("position_name"));
                positionListNode.add(positionNode);
            }
            return positionListNode;
        });
    }

    public ObjectNode queryPositionById(String id) {
        String sql = """
                SELECT position_id, position_name
                FROM positions
                WHERE position_id = :position_id
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("position_id", id);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            if (rs.next()) {
                return mapper.createObjectNode()
                        .put("positionId", rs.getString("position_id"))
                        .put("positionName", rs.getString("position_name"));
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

    public List<String> queryRolesByEmployeeId(String employeeId) {
        String sql = """
                SELECT r.role_name FROM users u
                JOIN user_roles as ur on u.user_id = ur.user_id
                JOIN roles as r on ur.role_id = r.role_id
                where u.employee_id = :employee_id
                """;
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("employee_id", employeeId);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            List<String> rolesName = new ArrayList<>();
            while (rs.next()) {
                rolesName.add(rs.getString("role_name"));
            }
            return rolesName;
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
