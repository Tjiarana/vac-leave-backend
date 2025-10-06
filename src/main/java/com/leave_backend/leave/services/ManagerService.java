package com.leave_backend.leave.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.db.InsertData;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.db.UpdateData;
import com.leave_backend.leave.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class ManagerService {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final QueryData queryData;
    private final InsertData insertData;
    private final UpdateData updateData;
    private final ObjectMapper mapper = JsonMapper.builder().build();

    public ResponseEntity<Object> getAllManager() {
        List<ObjectNode> queryResult = queryData.queryAllManager();
        if (queryResult.isEmpty()) {
            return ResponseMessage.generateResponseEntity(200, "Managers are empty");
        }
        ObjectNode responseData = mapper.createObjectNode();
        ArrayNode arrayNode = responseData.putArray("data");
        arrayNode.addAll((ArrayNode) mapper.valueToTree(queryResult));
        return ResponseMessage.generateResponseEntity(200, "Retrieve all managers successfully", responseData);
    }

    public ResponseEntity<Object> getOtherManager(String exceptId) {
        List<ObjectNode> queryResult = queryData.queryOtherManager(exceptId);
        if (queryResult.isEmpty()) {
            return ResponseMessage.generateResponseEntity(200, "Managers are empty");
        }
        ObjectNode responseData = mapper.createObjectNode();
        ArrayNode arrayNode = responseData.putArray("data");
        arrayNode.addAll((ArrayNode) mapper.valueToTree(queryResult));
        return ResponseMessage.generateResponseEntity(200, "Retrieve other managers successfully", responseData);
    }
}
