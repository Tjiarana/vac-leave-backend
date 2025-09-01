package com.leave_backend.leave.services;

import com.leave_backend.leave.models.Position;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class PositionService {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Position> positionRowMapper = new RowMapper<Position>() {
        @Override
        public Position mapRow(ResultSet rs, int rowNum) throws SQLException {
            return Position.builder()
                    .id(rs.getInt("position_id"))
                    .positionName(rs.getString("position_name"))
                    .build();
        }
    };

    public List<Position> getAll() {
        String sql = "SELECT * FROM positions";
        return jdbcTemplate.query(sql, positionRowMapper);
    }

    public Position getById(int id) {
        final String sql = """
                SELECT position_id, position_name
                FROM positions 
                WHERE position_id = ?
                """;
        try {
            return jdbcTemplate.queryForObject(sql, positionRowMapper, id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Position id " + id + " not found");
        }
    }
}
