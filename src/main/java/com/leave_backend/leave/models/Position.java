package com.leave_backend.leave.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class Position {
    private int id;
    private String positionName;
}
