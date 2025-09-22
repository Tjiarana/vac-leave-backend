package com.leave_backend.leave.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.leave_backend.leave.db.QueryData;
import com.leave_backend.leave.models.LoginRequestModel;
import com.leave_backend.leave.utils.JwtUtil;
import com.leave_backend.leave.utils.ResponseMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final ObjectMapper mapper = JsonMapper.builder().build();
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final QueryData queryData;

    public ResponseEntity<Object> login(LoginRequestModel request) {
        try {
            if (queryData.queryUserIdByEmployeeId(request.getEmployeeId()) == null) {
                return ResponseMessage.generateResponseEntity(400, "EMP_NOT_FOUND", "Employee not found with id: " + request.getEmployeeId());
            }
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(String.valueOf(request.getEmployeeId()), request.getPassword())
            );
            UserDetails user = (UserDetails) auth.getPrincipal();
            String token = jwtUtil.generateToken(user.getUsername(), user.getAuthorities());
            Date expiration = jwtUtil.getExpirationDateFromToken(token);
            ObjectNode responseEntity = mapper.createObjectNode();
            responseEntity.put("message", "Login Successfully");
            ObjectNode responseData = mapper.createObjectNode();
            responseData.put("token", token);
            ArrayNode rolesArray = mapper.createArrayNode();
            user.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .distinct()
                    .forEach(rolesArray::add);
            responseData.set("roles", rolesArray);
            responseData.put("token_expiration", expiration.toString());
            responseEntity.set("data", responseData);
            return ResponseMessage.generateResponseEntity(200, "Login Successfully", responseEntity);
        } catch (BadCredentialsException ex) {
            return ResponseMessage.generateResponseEntity(401, "EMP_INVALID_PASS", "Login failed, invalid password");
        }
    }
}
