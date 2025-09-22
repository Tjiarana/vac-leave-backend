package com.leave_backend.leave.controllers;

import com.leave_backend.leave.models.Position;
import com.leave_backend.leave.services.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/api/position")
@RestController
public class PositionController {
    private final PositionService positionService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getAllPosition() {
        return positionService.getAllPosition();
    }

    @GetMapping(value = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getPositionById(@PathVariable String id) {
        return positionService.getPositionById(id);
    }

}
