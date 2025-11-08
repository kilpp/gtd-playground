// ...existing code...
package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.model.Context;
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
public class ContextRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public ContextRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Context> mapper = (rs, _rowNum) -> new Context(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("name"),
            rs.getString("description"),
            rs.getBoolean("is_location"),
            rs.getTimestamp("created_at").toInstant()
    );

    public List<Context> findAll() {
        return jdbc.query("SELECT id, user_id, name, description, is_location, created_at FROM gtd.contexts", Collections.emptyMap(), mapper);
    }

    public List<Context> findByUserId(Long userId) {
        return jdbc.query("SELECT id, user_id, name, description, is_location, created_at FROM gtd.contexts WHERE user_id = :user_id", Map.of("user_id", userId), mapper);
    }

    public Optional<Context> findById(Long id) {
        Map<String, Object> params = Map.of("id", id);
        List<Context> l = jdbc.query("SELECT id, user_id, name, description, is_location, created_at FROM gtd.contexts WHERE id = :id", params, mapper);
        return l.stream().findFirst();
    }

    public Context create(CreateContextDto dto) throws DataIntegrityViolationException {
        String sql = "INSERT INTO gtd.contexts (user_id, name, description, is_location, created_at) VALUES (:user_id, :name, :description, :is_location, :created_at)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("description", dto.description())
                .addValue("is_location", dto.isLocation())
                .addValue("created_at", Timestamp.from(Instant.now()));
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created context"));
    }

    public Context update(Long id, CreateContextDto dto) {
        String sql = "UPDATE gtd.contexts SET user_id = :user_id, name = :name, description = :description, is_location = :is_location WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("description", dto.description())
                .addValue("is_location", dto.isLocation())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) return null;
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        int updated = jdbc.update("DELETE FROM gtd.contexts WHERE id = :id", Map.of("id", id));
        return updated > 0;
    }
}

