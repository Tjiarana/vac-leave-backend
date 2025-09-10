package com.leave_backend.leave.services;

import com.leave_backend.leave.db.DeleteData;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.User;
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
    private final DeleteData deleteData;

    @Transactional
    public ResponseEntity<Object> insertUser(User user) {
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
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_password", passwordEncoder.encode(user.getUserPassword()));
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
