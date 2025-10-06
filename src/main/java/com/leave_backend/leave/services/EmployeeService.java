package com.leave_backend.leave.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.db.UpdateData;
import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            return ResponseMessage.generateResponseEntity(200, "Employees are empty");
        } else {
            ObjectNode responseData = mapper.createObjectNode();
            ArrayNode arrayNode = responseData.putArray("data");
            arrayNode.addAll((ArrayNode) mapper.valueToTree(queryResult));
            return ResponseMessage.generateResponseEntity(200, "Retrieve all employee successfully", responseData);
        }
    }

    public ResponseEntity<Object> getAllEmployeeDTO() {
        List<ObjectNode> queryResult = queryData.queryAllEmployeeDTO();
        if (queryResult.isEmpty()) {
            return ResponseMessage.generateResponseEntity(200, "Employees are empty");
        }
       for (ObjectNode employeeNode : queryResult) {
           if (employeeNode.hasNonNull("reportTo")) {
               JsonNode managerId = employeeNode.get("reportTo");
               ObjectNode managerNode = queryData.queryEmployeeDTO(managerId.asText());
               employeeNode.set("reportTo", managerNode);
           } else {
               employeeNode.remove("reportTo");
           }
       }
       ObjectNode responseData = mapper.createObjectNode();
       ArrayNode arrayNode = responseData.putArray("data");
       arrayNode.addAll((ArrayNode) mapper.valueToTree(queryResult));
       return ResponseMessage.generateResponseEntity(200, "Retrieve all employee successfully", responseData);
    }

    public ResponseEntity<Object> getAllEmployeeDTO(String employeeId) {
        List<ObjectNode> queryResult = queryData.queryAllEmployeeDTO(employeeId);
        if (queryResult.isEmpty()) {
            return ResponseMessage.generateResponseEntity(200, "Employees are empty");
        } else {
            for (ObjectNode employeeNode : queryResult) {
                if (employeeNode.hasNonNull("reportTo")) {
                    JsonNode managerId = employeeNode.get("reportTo");
                    ObjectNode managerNode = queryData.queryEmployeeDTO(managerId.asText());
                    employeeNode.set("reportTo", managerNode);
                } else {
                    employeeNode.remove("reportTo");
                }
            }
            ObjectNode responseData = mapper.createObjectNode();
            ArrayNode arrayNode = responseData.putArray("data");
            arrayNode.addAll((ArrayNode) mapper.valueToTree(queryResult));
            return ResponseMessage.generateResponseEntity(200, "Retrieve all employee successfully", responseData);
        }
    }

    public ResponseEntity<Object> getEmployeeById(String id) {
        ObjectNode queryResult = queryData.queryEmployee(id);
        if (queryResult == null) {
            return ResponseMessage.generateResponseEntity(404, "EMP_NOT_FOUND", "Employees not found with id: " + id);
        } else {
            ObjectNode responseData = mapper.createObjectNode();
            responseData.set("data", queryResult);
            return ResponseMessage.generateResponseEntity(200, "Retrieve employee id: " + id + " successfully", responseData);
        }
    }

    public ResponseEntity<Object> getEmployeeDTOById(String id) {
        ObjectNode queryResult = queryData.queryEmployeeDTO(id);
        if (queryResult == null) {
            return ResponseMessage.generateResponseEntity(404, "EMP_NOT_FOUND", "Employees not found with id: " + id);
        }
        if (queryResult.hasNonNull("reportTo")) {
            JsonNode managerId = queryResult.get("reportTo");
            ObjectNode managerNode = queryData.queryEmployeeDTO(managerId.asText());
            queryResult.set("reportTo", managerNode);
        } else {
            queryResult.remove("reportTo");
        }
        ObjectNode responseData = mapper.createObjectNode();
        responseData.set("data", queryResult);
        return ResponseMessage.generateResponseEntity(200, "Retrieve employee id: " + id + " successfully", responseData);
    }

    @Transactional
    public ResponseEntity<Object> insertEmployee(Employee employee) {
        List<String> existingEmployeeId = queryData.queryAllEmployee().stream().map(Employee::getId).toList();
        Map<String, List<String>> existingEmployeeFields = queryData.queryExistingEmployeeField(employee.getEmployeeEmail(), employee.getEmployeePhone());
        if (queryData.queryExistingEmployee(employee.getId())) {
            return ResponseMessage.generateResponseEntity(409, "EMP_EXIST", "Employee id: " + employee.getId() + " already exist");
        }
        if (existingEmployeeFields != null) {
            if (existingEmployeeFields.get("emails").contains(employee.getEmployeeEmail())) {
                return ResponseMessage.generateResponseEntity(409, "EMP_EXIST_EMAIL", "Email: " + employee.getEmployeeEmail() + " already exist");
            }
            if (existingEmployeeFields.get("phones").contains(employee.getEmployeePhone())) {
                return ResponseMessage.generateResponseEntity(409, "EMP_EXIST_PHONE", "Phone number: " + employee.getEmployeePhone() + " already exist");
            }
        }
        if (employee.getReportTo() != null && !existingEmployeeId.contains(employee.getReportTo()) || employee.getId().equals(employee.getReportTo())) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_EMP_ID", "Invalid report to: " + employee.getReportTo());
        }
        if (queryData.queryExistingPosition(employee.getPositionId()) == false) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_POSITION_ID", "Invalid position id: " + employee.getPositionId());
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
            return ResponseMessage.generateResponseEntity(200, "Insert employee successfully");
        } else {
            return ResponseMessage.generateResponseEntity(500, "IN_ERR", "Cannot insert employee");
        }
    }

    @Transactional
    public ResponseEntity<Object> updateEmployee(Employee employee) {
        List<String> existingEmployeeId = queryData.queryAllEmployee().stream().map(Employee::getId).toList();
        Map<String, List<String>> existingOtherEmployeeFields = queryData.queryExistingOtherEmployeeField(employee.getId(), employee.getEmployeeEmail(), employee.getEmployeePhone());
        if (!existingEmployeeId.contains(employee.getId())) {
            return ResponseMessage.generateResponseEntity(404, "Employee id: " + employee.getId() + " not found");
        }
        if (existingOtherEmployeeFields != null) {
            if (existingOtherEmployeeFields.get("emails").contains(employee.getEmployeeEmail())) {
                return ResponseMessage.generateResponseEntity(409, "EMP_EXIST_EMAIL", "Email: " + employee.getEmployeeEmail() + " already exist");
            }
            if (existingOtherEmployeeFields.get("phones").contains(employee.getEmployeePhone())) {
                return ResponseMessage.generateResponseEntity(409, "EMP_EXIST_PHONE", "Phone number: " + employee.getEmployeePhone() + " already exist");
            }
        }
        if (employee.getReportTo() != null && !existingEmployeeId.contains(employee.getReportTo()) || employee.getId().equals(employee.getReportTo())) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_EMP_ID", "Invalid report to: " + employee.getReportTo());
        }
        if (queryData.queryExistingPosition(employee.getPositionId()) == false) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_POSITION_ID", "Invalid position id: " + employee.getPositionId());
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
            return ResponseMessage.generateResponseEntity(200, "Update employee successfully");
        } else {
            return ResponseMessage.generateResponseEntity(500, "IN_ERR", "Cannot update employee");
        }
    }
}
