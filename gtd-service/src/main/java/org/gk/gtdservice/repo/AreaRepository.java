package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.model.Area;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class AreaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public AreaRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Area> mapper = (rs, _rowNum) -> new Area(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getTimestamp("created_at").toInstant()
    );

    public List<Area> findAll() {
        return jdbc.query("SELECT id, user_id, name, description, created_at FROM gtd.areas", Collections.emptyMap(), mapper);
    }

    public List<Area> findByUserId(Long userId) {
        return jdbc.query("SELECT id, user_id, name, description, created_at FROM gtd.areas WHERE user_id = :user_id", Map.of("user_id", userId), mapper);
    }

    public Optional<Area> findById(Long id) {
        Map<String, Object> params = Map.of("id", id);
        List<Area> l = jdbc.query("SELECT id, user_id, name, description, created_at FROM gtd.areas WHERE id = :id", params, mapper);
        return l.stream().findFirst();
    }

    public Area create(CreateAreaDto dto) throws DataIntegrityViolationException {
        String sql = "INSERT INTO gtd.areas (user_id, name, description, created_at) VALUES (:user_id, :name, :description, :created_at)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("description", dto.description())
                .addValue("created_at", Timestamp.from(Instant.now()));
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created area"));
    }

    public Area update(Long id, CreateAreaDto dto) {
        String sql = "UPDATE gtd.areas SET user_id = :user_id, name = :name, description = :description WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("description", dto.description())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) return null;
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        int updated = jdbc.update("DELETE FROM gtd.areas WHERE id = :id", Map.of("id", id));
        return updated > 0;
    }
}

