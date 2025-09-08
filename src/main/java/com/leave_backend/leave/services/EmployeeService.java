package com.leave_backend.leave.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
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

@Slf4j
@RequiredArgsConstructor
@Service
public class EmployeeService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final QueryData queryData;
    private final InsertData insertData;
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

    public ResponseEntity<Object> getEmployeeById(Long id) {
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

    public ResponseEntity<Object> getEmployeeDTOById(Long id) {
        ObjectNode queryResult = queryData.queryEmployeeDTO(id);
        if (queryResult == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status("error")
                    .message("Employees not found with id: " + id)
                    .build());
        }
        if (queryResult.hasNonNull("reportTo")) {
            JsonNode managerId = queryResult.get("reportTo");
            Employee reportToQueryResult = queryData.queryEmployee(managerId.asLong());
            ObjectNode managerNode = mapper.createObjectNode();
            managerNode.put("name", reportToQueryResult.getEmployeeFirstname() + " " + reportToQueryResult.getEmployeeLastname());
            managerNode.put("position", queryData.queryPosition(reportToQueryResult.getPositionId()));
            queryResult.set("reportTo", managerNode);
        } else {
            queryResult.remove("reportTo");
        }

        JsonNode positionId = queryResult.get("positionId");
        String positionName = queryData.queryPosition(positionId.asInt());
        queryResult.remove("positionId");
        queryResult.put("position", positionName);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status("success")
                .message("Retrieve employee id: " + id + " successfully")
                .data(queryResult)
                .build());
    }

    @Transactional
    public ResponseEntity<Object> insertEmployee(Employee employee) {
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
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("error")
                    .message("Cannot insert employee")
                    .build());
        }
    }
}
