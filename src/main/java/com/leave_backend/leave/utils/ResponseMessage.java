package com.leave_backend.leave.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public class ResponseMessage {
    public static ObjectNode generateResponseMessage(String message, ObjectNode responseNode) {
        return generateResponseMessage(null, message, responseNode);
    }

    public static ObjectNode generateResponseMessage(String code, String message) {
        return generateResponseMessage(code, message, null);
    }

    public static ObjectNode generateResponseMessage(String code, String message, ObjectNode responseNode) {
        ObjectMapper mapper = JsonMapper.builder().build();
        ObjectNode node = mapper.createObjectNode();
        if (code != null && !code.isBlank()) {
            node.put("code", code);
        }
        node.put("message", message);
        if (responseNode != null) {
            node.setAll(responseNode);
        }
        return node;
    }

    public static ResponseEntity<Object> generateResponseEntity(int status, String message, ObjectNode responseNode) {
        return generateResponseEntity(status, null, message, responseNode);
    }

    public static ResponseEntity<Object> generateResponseEntity(int status, String code, String message) {
        return generateResponseEntity(status, code, message, null);
    }

    public static ResponseEntity<Object> generateResponseEntity(int status, String message) {
        return generateResponseEntity(status, null, message, null);
    }

    public static ResponseEntity<Object> generateResponseEntity(int status, String code, String message, ObjectNode responseNode) {
        ObjectNode newResponseNode = generateResponseMessage(code, message, responseNode);
        return ResponseEntity.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(newResponseNode);
    }

}
