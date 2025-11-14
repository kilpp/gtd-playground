package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.model.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ContextRepository.class);

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
        logger.info("Finding all contexts");
        List<Context> contexts = jdbc.query("SELECT id, user_id, name, description, is_location, created_at FROM gtd.contexts", Collections.emptyMap(), mapper);
        logger.debug("Found {} contexts", contexts.size());
        return contexts;
    }

    public List<Context> findByUserId(Long userId) {
        logger.info("Finding contexts by userId: {}", userId);
        List<Context> contexts = jdbc.query("SELECT id, user_id, name, description, is_location, created_at FROM gtd.contexts WHERE user_id = :user_id", Map.of("user_id", userId), mapper);
        logger.debug("Found {} contexts for userId: {}", contexts.size(), userId);
        return contexts;
    }

    public Optional<Context> findById(Long id) {
        logger.info("Finding context by id: {}", id);
        Map<String, Object> params = Map.of("id", id);
        List<Context> l = jdbc.query("SELECT id, user_id, name, description, is_location, created_at FROM gtd.contexts WHERE id = :id", params, mapper);
        Optional<Context> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found context with id: {}", id);
        } else {
            logger.debug("Context not found with id: {}", id);
        }
        return result;
    }

    public Context create(CreateContextDto dto) throws DataIntegrityViolationException {
        logger.info("Creating context: {}", dto);
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
        logger.info("Created context with id: {}", id);
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created context"));
    }

    public Context update(Long id, CreateContextDto dto) {
        logger.info("Updating context with id: {}, dto: {}", id, dto);
        String sql = "UPDATE gtd.contexts SET user_id = :user_id, name = :name, description = :description, is_location = :is_location WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("description", dto.description())
                .addValue("is_location", dto.isLocation())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            logger.warn("No context updated for id: {}", id);
            return null;
        }
        logger.info("Updated context with id: {}", id);
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        logger.info("Deleting context with id: {}", id);
        int updated = jdbc.update("DELETE FROM gtd.contexts WHERE id = :id", Map.of("id", id));
        boolean deleted = updated > 0;
        if (deleted) {
            logger.info("Deleted context with id: {}", id);
        } else {
            logger.warn("No context deleted for id: {}", id);
        }
        return deleted;
    }
}
