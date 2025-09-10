package com.leave_backend.leave.services;

import com.leave_backend.leave.models.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
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
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Position> getAll() {
        String sql = "SELECT position_id, position_name FROM positions";
        return namedParameterJdbcTemplate.query(sql, (rs, rowNum) -> {
            return new Position(rs.getString("position_id"), rs.getString("position_name"));
        });
    }

    public String getPositionById(String id) {
        final String sql = """
                SELECT position_name
                FROM positions
                WHERE position_id = :position_id
                """;
        MapSqlParameterSource parameters = new MapSqlParameterSource().addValue("position_id", id);
        return namedParameterJdbcTemplate.query(sql, parameters, rs -> {
            return rs.getString("position_name");
        });
    }
}
