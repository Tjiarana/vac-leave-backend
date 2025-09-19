package com.leave_backend.leave.services;

import com.leave_backend.leave.db.DeleteData;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.db.UpdateData;
import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.Employee;
import com.leave_backend.leave.models.User;
import com.leave_backend.leave.models.UserRequestModel;
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
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                    .status("error")
                    .message("User with employee id: " + user.getEmployeeId() + " already exist")
                    .build());
        }
        List<String> rolesId = queryData.queryRolesIdByRolesName(user.getRoles());
        if (rolesId == null || rolesId.size() != user.getRoles().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid role")
                    .build());
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
            return ResponseEntity.ok(ResponseDTO.builder()
                    .status("success")
                    .message("Create user successfully")
                    .build());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Cannot insert user")
                    .build());
        }
    }

    public ResponseEntity<Object> updateUser(UserRequestModel user) {
        List<String> existingEmployeeId = queryData.queryAllEmployee().stream().map(Employee::getId).toList();
        Map<String, List<String>> existingOtherEmployeeFields = queryData.queryExistingOtherEmployeeField(user.getEmployeeId(), user.getEmployeeEmail(), user.getEmployeePhone());
        List<String> rolesId = queryData.queryRolesIdByRolesName(user.getRoles());
        if (rolesId == null || rolesId.size() != user.getRoles().size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid role")
                    .build());
        }
        if (!existingEmployeeId.contains(user.getEmployeeId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status("error")
                    .message("Employee id: " + user.getEmployeeId() + " not found")
                    .build());
        }
        if (existingOtherEmployeeFields != null) {
            if (existingOtherEmployeeFields.get("emails").contains(user.getEmployeeEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                        .status("error")
                        .message("Email: " + user.getEmployeeEmail() + " already exist")
                        .build());
            }
            if (existingOtherEmployeeFields.get("phones").contains(user.getEmployeePhone())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseDTO.builder()
                        .status("error")
                        .message("Phone number: " + user.getEmployeePhone() + " already exist")
                        .build());
            }
        }
        if (user.getReportTo() != null && !existingEmployeeId.contains(user.getReportTo()) || user.getEmployeeId().equals(user.getReportTo())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid report to: " + user.getReportTo())
                    .build());
        }
        if (queryData.queryExistingPosition(user.getPositionId()) == false) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDTO.builder()
                    .status("error")
                    .message("Invalid position id: " + user.getPositionId())
                    .build());
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
        return ResponseEntity.ok(ResponseDTO.builder()
                .status("success")
                .message("Update user successfully")
                .build());
    }

    public ResponseEntity<Object> deleteUser(String employeeId) {
        String userId = queryData.queryUserIdByEmployeeId(employeeId);
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDTO.builder()
                    .status("error")
                    .message("User with employee id: " + employeeId + " not found")
                    .build());
        }
        deleteData.deleteUserRoles(userId);
        deleteData.deleteUser(userId);
        deleteData.deleteEmployee(employeeId);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status("success")
                .message("Delete user with employee id: " + employeeId + " successfully")
                .build());
    }
}
