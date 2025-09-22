package com.leave_backend.leave.services;

import com.leave_backend.leave.db.DeleteData;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.db.UpdateData;
import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.models.User;
import com.leave_backend.leave.models.UserRequestModel;
import com.leave_backend.leave.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final QueryData queryData;
    private final InsertData insertData;
    private final UpdateData updateData;
    private final DeleteData deleteData;

    private final EmployeeService employeeService;

    @Transactional
    public ResponseEntity<Object> insertUser(UserRequestModel user) {
        boolean isUserAlreadyExist = queryData.queryUserIdByEmployeeId(user.getEmployeeId()) != null;
        if (isUserAlreadyExist) {
            return ResponseMessage.generateResponseEntity(409, "EMP_EXIST", "Employee id: " + user.getEmployeeId() + " already exist");
        }
        List<String> rolesId = queryData.queryRolesIdByRolesName(user.getRoles());
        if (rolesId == null || rolesId.size() != user.getRoles().size()) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_ROLE", "Invalid role");
        }
        Employee employeeInfo = Employee.builder()
                .id(user.getEmployeeId())
                .employeeFirstname(user.getEmployeeFirstname())
                .employeeLastname(user.getEmployeeLastname())
                .employeeGender(user.getEmployeeGender())
                .employeeEmail(user.getEmployeeEmail())
                .employeePhone(user.getEmployeePhone())
                .reportTo(user.getReportTo())
                .positionId(user.getPositionId()).build();
        employeeService.insertEmployee(employeeInfo);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_password", passwordEncoder.encode("password" + user.getEmployeeId()));
        userInfo.put("employee_id", user.getEmployeeId());
        int insertResult = insertData.insertUser(userInfo);
        if (insertResult == 1) {
            String userId = queryData.queryUserIdByEmployeeId(user.getEmployeeId());
            insertData.insertUserRoles(userId, rolesId);
            return ResponseMessage.generateResponseEntity(200, "Create user successfully");
        } else {
            return ResponseMessage.generateResponseEntity(500, "IN_ERR", "Cannot insert user");
        }
    }

    public ResponseEntity<Object> updateUser(UserRequestModel user) {
        List<String> existingEmployeeId = queryData.queryAllEmployee().stream().map(Employee::getId).toList();
        Map<String, List<String>> existingOtherEmployeeFields = queryData.queryExistingOtherEmployeeField(user.getEmployeeId(), user.getEmployeeEmail(), user.getEmployeePhone());
        List<String> rolesId = queryData.queryRolesIdByRolesName(user.getRoles());
        if (rolesId == null || rolesId.size() != user.getRoles().size()) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_ROLE", "Invalid role");
        }
        if (!existingEmployeeId.contains(user.getEmployeeId())) {
            return ResponseMessage.generateResponseEntity(404, "EMP_NOT_FOUND", "Employees not found with id: " + user.getEmployeeId());
        }
        if (existingOtherEmployeeFields != null) {
            if (existingOtherEmployeeFields.get("emails").contains(user.getEmployeeEmail())) {
                return ResponseMessage.generateResponseEntity(409, "EMP_EXIST_EMAIL", "Email: " + user.getEmployeeEmail() + " already exist");
            }
            if (existingOtherEmployeeFields.get("phones").contains(user.getEmployeePhone())) {
                return ResponseMessage.generateResponseEntity(409, "EMP_EXIST_PHONE", "Phone number: " + user.getEmployeePhone() + " already exist");
            }
        }
        if (user.getReportTo() != null && !existingEmployeeId.contains(user.getReportTo()) || user.getEmployeeId().equals(user.getReportTo())) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_EMP_ID", "Invalid report to: " + user.getReportTo());
        }
        if (queryData.queryExistingPosition(user.getPositionId()) == false) {
            return ResponseMessage.generateResponseEntity(400, "INVALID_POSITION_ID", "Invalid position id: " + user.getPositionId());
        }
        Employee employeeInfo = Employee.builder()
                .id(user.getEmployeeId())
                .employeeFirstname(user.getEmployeeFirstname())
                .employeeLastname(user.getEmployeeLastname())
                .employeeGender(user.getEmployeeGender())
                .employeeEmail(user.getEmployeeEmail())
                .employeePhone(user.getEmployeePhone())
                .reportTo(user.getReportTo())
                .positionId(user.getPositionId()).build();
        employeeService.updateEmployee(employeeInfo);
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_password", passwordEncoder.encode(user.getUserPassword()));
        userInfo.put("employee_enabled", true);
        userInfo.put("employee_id", user.getEmployeeId());
        updateData.updateUser(userInfo);
        String userId = queryData.queryUserIdByEmployeeId(user.getEmployeeId());
        deleteData.deleteUserRoles(userId);
        insertData.insertUserRoles(userId, rolesId);
        return ResponseMessage.generateResponseEntity(200, "Update user successfully");
    }

    public ResponseEntity<Object> deleteUser(String employeeId) {
        String userId = queryData.queryUserIdByEmployeeId(employeeId);
        if (userId == null) {
            return ResponseMessage.generateResponseEntity(404, "EMP_NOT_FOUND", "User with employee id: " + employeeId + " not found");
        }
        deleteData.deleteUserRoles(userId);
        deleteData.deleteUser(userId);
        deleteData.deleteEmployee(employeeId);
        return ResponseMessage.generateResponseEntity(200, "Delete user with employee id: " + employeeId + " successfully");
    }
}
