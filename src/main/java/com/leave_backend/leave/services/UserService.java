package com.leave_backend.leave.services;

import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.dto.ResponseDTO;
import com.leave_backend.leave.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final QueryData queryData;
    private final InsertData insertData;
    private final UserRoleService userRoleService;

    public ResponseEntity<Object> insertUser(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("user_password", passwordEncoder.encode(user.getUserPassword()));
        userInfo.put("employee_id", user.getEmployeeId());
        int insertResult = insertData.insertUser(userInfo);
        if (insertResult == 1) {
            List<Long> rolesId = queryData.queryRolesIdByRolesName(user.getRoles());
            int userId = queryData.queryUserIdByEmployeeId((long) user.getEmployeeId());
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
}
