package com.leave_backend.leave.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.db.UpdateData;
import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.Employee;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final QueryData queryData;
    private final InsertData insertData;
    private final UpdateData updateData;
    private final ObjectMapper mapper = JsonMapper.builder().build();

    public ResponseEntity<Object> getAllEmployee() {
        List<Employee> queryResult = queryData.queryAllEmployee();
        if (queryResult.isEmpty()) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Employees are empty")
                    .build());
        } else {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Retrieve all employee successfully")
                    .data(queryResult)
                    .build());
        }
    }

    public ResponseEntity<Object> getAllEmployeeDTO() {
        List<ObjectNode> queryResult = queryData.queryAllEmployeeDTO();
        if (queryResult.isEmpty()) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Employees are empty")
                    .build());
        } else {
            for (ObjectNode employeeNode : queryResult) {
                if (employeeNode.hasNonNull("reportTo")) {
                    JsonNode managerId = employeeNode.get("reportTo");
                    Employee reportToQueryResult = queryData.queryEmployee(managerId.asText());
                    ObjectNode managerNode = mapper.createObjectNode();
                    managerNode.put("name", reportToQueryResult.getEmployeeFirstname() + " " + reportToQueryResult.getEmployeeLastname());
                    managerNode.put("position", queryData.queryPosition(reportToQueryResult.getPositionId()));
                    employeeNode.set("reportTo", managerNode);
                } else {
                    employeeNode.remove("reportTo");
                }
            }
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Retrieve all employee successfully")
                    .data(queryResult)
                    .build());
        }
    }

    public ResponseEntity<Object> getAllEmployeeDTO(String employeeId) {
        List<ObjectNode> queryResult = queryData.queryAllEmployeeDTO(employeeId);
        if (queryResult.isEmpty()) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Employees are empty")
                    .build());
        } else {
            for (ObjectNode employeeNode : queryResult) {
                if (employeeNode.hasNonNull("reportTo")) {
                    JsonNode managerId = employeeNode.get("reportTo");
                    Employee reportToQueryResult = queryData.queryEmployee(managerId.asText());
                    ObjectNode managerNode = mapper.createObjectNode();
                    managerNode.put("name", reportToQueryResult.getEmployeeFirstname() + " " + reportToQueryResult.getEmployeeLastname());
                    managerNode.put("position", queryData.queryPosition(reportToQueryResult.getPositionId()));
                    employeeNode.set("reportTo", managerNode);
                } else {
                    employeeNode.remove("reportTo");
                }
            }
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Retrieve all employee successfully")
                    .data(queryResult)
                    .build());
        }
    }

    public ResponseEntity<Object> getEmployeeById(String id) {
        Employee queryResult = queryData.queryEmployee(id);
        if (queryResult == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status("error")
                    .message("Employees not found with id: " + id)
                    .build());
        } else {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Retrieve employee id: " + id + " successfully")
                    .data(queryResult)
                    .build());
        }
    }

    public ResponseEntity<Object> getEmployeeDTOById(String id) {
        ObjectNode queryResult = queryData.queryEmployeeDTO(id);
        if (queryResult == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status("error")
                    .message("Employees not found with id: " + id)
                    .build());
        }
        if (queryResult.hasNonNull("reportTo")) {
            JsonNode managerId = queryResult.get("reportTo");
            Employee reportToQueryResult = queryData.queryEmployee(managerId.asText());
            ObjectNode managerNode = mapper.createObjectNode();
            managerNode.put("name", reportToQueryResult.getEmployeeFirstname() + " " + reportToQueryResult.getEmployeeLastname());
            managerNode.put("position", queryData.queryPosition(reportToQueryResult.getPositionId()));
            queryResult.set("reportTo", managerNode);
        } else {
            queryResult.remove("reportTo");
        }
        return ResponseEntity.ok(ResponseDTO.builder()
                .status("success")
                .message("Retrieve employee id: " + id + " successfully")
                .data(queryResult)
                .build());
    }

    @Transactional
    public ResponseEntity<Object> insertEmployee(Employee employee) {
        List<String> existingEmployeeId = queryData.queryAllEmployee().stream().map(Employee::getId).toList();
        Map<String, List<String>> existingEmployeeFields = queryData.queryExistingEmployeeField(employee.getEmployeeEmail(), employee.getEmployeePhone());
        if (queryData.queryExistingEmployee(employee.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                    .status("error")
                    .message("Employee id: " + employee.getId() + " already exist")
                    .build());
        }
        if (existingEmployeeFields != null) {
            if (existingEmployeeFields.get("emails").contains(employee.getEmployeeEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                        .status("error")
                        .message("Email: " + employee.getEmployeeEmail() + " already exist")
                        .build());
            }
            if (existingEmployeeFields.get("phones").contains(employee.getEmployeePhone())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                        .status("error")
                        .message("Phone number: " + employee.getEmployeePhone() + " already exist")
                        .build());
            }
        }
        if (employee.getReportTo() != null && !existingEmployeeId.contains(employee.getReportTo()) || employee.getId().equals(employee.getReportTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid report to: " + employee.getReportTo())
                    .build());
        }
        if (queryData.queryExistingPosition(employee.getPositionId()) == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid position id: " + employee.getPositionId())
                    .build());
        }
        HashMap<String, Object> employeeInfo = new HashMap<>();
        employeeInfo.put("employee_id", employee.getId());
        employeeInfo.put("employee_firstname", employee.getEmployeeFirstname());
        employeeInfo.put("employee_lastname", employee.getEmployeeLastname());
        employeeInfo.put("employee_gender", employee.getEmployeeGender().toString());
        employeeInfo.put("employee_email", employee.getEmployeeEmail());
        employeeInfo.put("employee_phone", employee.getEmployeePhone());
        employeeInfo.put("report_to", employee.getReportTo());
        employeeInfo.put("position_id", employee.getPositionId());
        int insertResult = insertData.insertEmployee(employeeInfo);
        if (insertResult == 1) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Insert employee successfully")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.builder()
                    .status("error")
                    .message("Cannot insert employee")
                    .build());
        }
    }

    @Transactional
    public ResponseEntity<Object> updateEmployee(Employee employee) {
        List<String> existingEmployeeId = queryData.queryAllEmployee().stream().map(Employee::getId).toList();
        Map<String, List<String>> existingOtherEmployeeFields = queryData.queryExistingOtherEmployeeField(employee.getId(), employee.getEmployeeEmail(), employee.getEmployeePhone());
        if (!existingEmployeeId.contains(employee.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status("error")
                    .message("Employee id: " + employee.getId() + " not found")
                    .build());
        }
        if (existingOtherEmployeeFields != null) {
            if (existingOtherEmployeeFields.get("emails").contains(employee.getEmployeeEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                        .status("error")
                        .message("Email: " + employee.getEmployeeEmail() + " already exist")
                        .build());
            }
            if (existingOtherEmployeeFields.get("phones").contains(employee.getEmployeePhone())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                        .status("error")
                        .message("Phone number: " + employee.getEmployeePhone() + " already exist")
                        .build());
            }
        }
        if (employee.getReportTo() != null && !existingEmployeeId.contains(employee.getReportTo()) || employee.getId().equals(employee.getReportTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid report to: " + employee.getReportTo())
                    .build());
        }
        if (queryData.queryExistingPosition(employee.getPositionId()) == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid position id: " + employee.getPositionId())
                    .build());
        }
        Map<String, Object> employeeInfo = new HashMap<>();
        employeeInfo.put("employee_firstname", employee.getEmployeeFirstname());
        employeeInfo.put("employee_lastname", employee.getEmployeeLastname());
        employeeInfo.put("employee_gender", employee.getEmployeeGender().toString());
        employeeInfo.put("employee_email", employee.getEmployeeEmail());
        employeeInfo.put("employee_phone", employee.getEmployeePhone());
        employeeInfo.put("report_to", employee.getReportTo());
        employeeInfo.put("position_id", employee.getPositionId());
        int updatedResult = updateData.updateEmployee(employee.getId(), employeeInfo);
        if (updatedResult == 1) {
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Update employee successfully")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDTO.builder()
                    .status("error")
                    .message("Cannot update employee")
                    .build());
        }
    }
}
