package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.model.Area;
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
public class AreaRepository {

    private static final Logger logger = LoggerFactory.getLogger(AreaRepository.class);

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
        logger.info("Finding all areas");
        List<Area> areas = jdbc.query("SELECT id, user_id, name, description, created_at FROM gtd.areas", Collections.emptyMap(), mapper);
        logger.debug("Found {} areas", areas.size());
        return areas;
    }

    public List<Area> findByUserId(Long userId) {
        logger.info("Finding areas by userId: {}", userId);
        List<Area> areas = jdbc.query("SELECT id, user_id, name, description, created_at FROM gtd.areas WHERE user_id = :user_id", Map.of("user_id", userId), mapper);
        logger.debug("Found {} areas for userId: {}", areas.size(), userId);
        return areas;
    }

    public Optional<Area> findById(Long id) {
        logger.info("Finding area by id: {}", id);
        Map<String, Object> params = Map.of("id", id);
        List<Area> l = jdbc.query("SELECT id, user_id, name, description, created_at FROM gtd.areas WHERE id = :id", params, mapper);
        Optional<Area> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found area with id: {}", id);
        } else {
            logger.debug("Area not found with id: {}", id);
        }
        return result;
    }

    public Area create(CreateAreaDto dto) throws DataIntegrityViolationException {
        logger.info("Creating area: {}", dto);
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
        logger.info("Created area with id: {}", id);
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created area"));
    }

    public Area update(Long id, CreateAreaDto dto) {
        logger.info("Updating area with id: {}, dto: {}", id, dto);
        String sql = "UPDATE gtd.areas SET user_id = :user_id, name = :name, description = :description WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("description", dto.description())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            logger.warn("No area updated for id: {}", id);
            return null;
        }
        logger.info("Updated area with id: {}", id);
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        logger.info("Deleting area with id: {}", id);
        int updated = jdbc.update("DELETE FROM gtd.areas WHERE id = :id", Map.of("id", id));
        boolean deleted = updated > 0;
        if (deleted) {
            logger.info("Deleted area with id: {}", id);
        } else {
            logger.warn("No area deleted for id: {}", id);
        }
        return deleted;
    }
}
