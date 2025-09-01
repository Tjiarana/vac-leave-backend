package com.leave_backend.leave.controllers;

import com.leave_backend.leave.models.Position;
import com.leave_backend.leave.services.PositionService;
import lombok.RequiredArgsConstructor;
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

    @GetMapping
    public ResponseEntity<List<Position>> getAll() {
        List<Position> positions = positionService.getAll();
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Position> getById(@PathVariable int id) {
        Position position = positionService.getById(id);
        return ResponseEntity.ok(position);
    }

}
