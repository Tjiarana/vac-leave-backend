package com.leave_backend.leave.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.models.Position;
import com.leave_backend.leave.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PositionService {
    private final QueryData queryData;
    private final ObjectMapper mapper = JsonMapper.builder().build();

    public ResponseEntity<Object> getAllPosition() {
        List<ObjectNode> queryResult = queryData.queryAllPosition();
        if (queryResult.isEmpty()) {
            return ResponseMessage.generateResponseEntity(200, "Positions are empty");
        }
        ObjectNode responseData = mapper.createObjectNode();
        ArrayNode arrayNode = responseData.putArray("data");
        arrayNode.addAll((ArrayNode) mapper.valueToTree(queryResult));
        return ResponseMessage.generateResponseEntity(200, "Retrieve all position successfully", responseData);
    }

    public ResponseEntity<Object> getPositionById(String id) {
        ObjectNode queryResult = queryData.queryPositionById(id);
        if (queryResult == null) {
            return ResponseMessage.generateResponseEntity(400, "INV_ID", "Invalid position id");
        }
        ObjectNode responseData = mapper.createObjectNode();
        responseData.set("data", queryResult);
        return ResponseMessage.generateResponseEntity(200, "Retrieve position successfully", responseData);
    }
}
