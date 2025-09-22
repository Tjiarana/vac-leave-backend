package com.leave_backend.leave.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.db.DeleteData;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.db.UpdateData;
import com.leave_backend.leave.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleService {
    private final QueryData queryData;
    private final InsertData insertData;
    private final UpdateData updateData;
    private final DeleteData deleteData;

    private final ObjectMapper mapper = JsonMapper.builder().build();

    public ResponseEntity<Object> getUserRolesByEmployeeId(String employeeId) {
        List<String> queryResult = queryData.queryRolesByEmployeeId(employeeId);
        if (queryResult.isEmpty()) {
            return ResponseMessage.generateResponseEntity(500, "IN_ERR", "Cannot get user role");
        }
        ObjectNode responseData = mapper.createObjectNode();
        ArrayNode arrayNode = responseData.putArray("data");
        arrayNode.addAll((ArrayNode) mapper.valueToTree(queryResult));
        return ResponseMessage.generateResponseEntity(200, "Retrieve role(s) employee id " + employeeId + " successfully", responseData);
    }
}
