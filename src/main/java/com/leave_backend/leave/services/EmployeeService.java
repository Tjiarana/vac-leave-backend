package com.leave_backend.leave.services;

import com.leave_backend.leave.RowMapper.EmployeeRowMapper;
import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final EmployeeRowMapper employeeRowMapper;
    private final PositionService positionService;

    public ResponseEntity<ResponseDTO> getAllEmployee() {
        String sql = """
                SELECT employee_id, employee_firstname, employee_lastname, employee_gender, employee_email, employee_phone, report_to, position_id
                FROM employees
                """;
        List<Employee> queryResult = namedParameterJdbcTemplate.query(sql, employeeRowMapper);
        if (queryResult.isEmpty()) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Employees is empty")
                    .build());
        } else {
            List<Object> employees = new ArrayList<>(queryResult);
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Retrieve all employee successfully")
                    .data(employees)
                    .build());
        }
    }

//    public Employee getEmployeeById(Long id) {
//        String sql = """
//                SELECT employee_id, employee_firstname, employee_lastname, employee_gender, employee_email, employee_phone, report_to, position_id
//                FROM employees
//                WHERE employee_id = :employee_id
//                """;
//        Map<String, Object> params = new HashMap<>();
//        params.put("employee_id", id);
//        return namedParameterJdbcTemplate.queryForObject(sql, params, employeeRowMapper);
//    }
//
//    public EmployeeDTO getEmployeeDTOById(Long id) {
//        final String sql = """
//                SELECT e.employee_id, e.employee_firstname, e.employee_lastname, e.employee_gender, e.employee_email, e.employee_phone,e.report_to, e.position_id,
//                m.employee_id as manager_id, m.employee_firstname as manager_firstname, m.employee_lastname as manager_lastname, m.position_id as manager_position_id
//                FROM employees e
//                LEFT JOIN employees m ON e.report_to = m.employee_id
//                WHERE e.employee_id = :employee_id
//                """;
//        Map<String, Object> params = new HashMap<>();
//        params.put("employee_id", id);
//
//        return namedParameterJdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
//            int positionId = rs.getInt("position_id");
//            Position position = positionService.getById(positionId);
//
//            Long managerId = rs.getObject("manager_id", Long.class);
//            EmployeeDTO manager = null;
//            if (managerId != null) {
//                int managerPositionId = rs.getInt("manager_position_id");
//                Position managerPosition = positionService.getById(managerPositionId);
//
//                manager = EmployeeDTO.builder()
//                        .id(managerId)
//                        .name(rs.getString("manager_firstname") + " " + rs.getString("manager_lastname"))
//                        .position(managerPosition.getPositionName())
//                        .reportTo(null)
//                        .build();
//            }
//
//            return EmployeeDTO.builder()
//                    .id(rs.getLong("employee_id"))
//                    .name(rs.getString("employee_firstname") + " " + rs.getString("employee_lastname"))
//                    .position(position.getPositionName())
//                    .reportTo(manager)
//                    .build();
//        });
//    }
//
//    @Transactional
//    public int addEmployee(Employee employee) {
//        String sql = """
//                INSERT INTO employees (employee_id, employee_firstname, employee_lastname, employee_gender, employee_email, employee_phone, report_to, position_id)
//                VALUES (:employee_id, :employee_firstname, :employee_lastname, :employee_gender, :employee_email, :employee_phone, :report_to, :position_id)
//                """;
//        Map<String, Object> params = new HashMap<>();
//        params.put("employee_id", employee.getId());
//        params.put("employee_firstname", employee.getEmployeeFirstname());
//        params.put("employee_lastname", employee.getEmployeeLastname());
//        params.put("employee_gender", employee.getEmployeeGender().name());
//        params.put("employee_email", employee.getEmployeeEmail());
//        params.put("employee_phone", employee.getEmployeePhone());
//        params.put("report_to", employee.getReportTo());
//        params.put("position_id", employee.getPositionId());
//        try {
//            return namedParameterJdbcTemplate.update(sql, params);
//        }
//    }
}
