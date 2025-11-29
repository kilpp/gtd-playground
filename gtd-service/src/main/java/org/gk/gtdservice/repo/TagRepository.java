package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.model.Tag;
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
public class TagRepository {

    private static final Logger logger = LoggerFactory.getLogger(TagRepository.class);

    private final NamedParameterJdbcTemplate jdbc;

    public TagRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Tag> mapper = (rs, _rowNum) -> new Tag(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("name"),
            rs.getTimestamp("created_at").toInstant()
    );

    public List<Tag> findAll() {
        logger.info("Finding all tags");
        List<Tag> tags = jdbc.query("SELECT id, user_id, name, created_at FROM gtd.tags", Collections.emptyMap(), mapper);
        logger.debug("Found {} tags", tags.size());
        return tags;
    }

    public List<Tag> findByUserId(Long userId) {
        logger.info("Finding tags by userId: {}", userId);
        List<Tag> tags = jdbc.query("SELECT id, user_id, name, created_at FROM gtd.tags WHERE user_id = :user_id", Map.of("user_id", userId), mapper);
        logger.debug("Found {} tags for userId: {}", tags.size(), userId);
        return tags;
    }

    public Optional<Tag> findById(Long id) {
        logger.info("Finding tag by id: {}", id);
        Map<String, Object> params = Map.of("id", id);
        List<Tag> l = jdbc.query("SELECT id, user_id, name, created_at FROM gtd.tags WHERE id = :id", params, mapper);
        Optional<Tag> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found tag with id: {}", id);
        } else {
            logger.debug("Tag not found with id: {}", id);
        }
        return result;
    }

    public Tag create(CreateTagDto dto) throws DataIntegrityViolationException {
        logger.info("Creating tag: {}", dto);
        String sql = "INSERT INTO gtd.tags (user_id, name, created_at) VALUES (:user_id, :name, :created_at)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("created_at", Timestamp.from(Instant.now()));
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        logger.info("Created tag with id: {}", id);
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created tag"));
    }

    public Tag update(Long id, CreateTagDto dto) {
        logger.info("Updating tag with id: {}, dto: {}", id, dto);
        String sql = "UPDATE gtd.tags SET user_id = :user_id, name = :name WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("name", dto.name())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            logger.warn("No tag updated for id: {}", id);
            return null;
        }
        logger.info("Updated tag with id: {}", id);
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        logger.info("Deleting tag with id: {}", id);
        int updated = jdbc.update("DELETE FROM gtd.tags WHERE id = :id", Map.of("id", id));
        boolean deleted = updated > 0;
        if (deleted) {
            logger.info("Deleted tag with id: {}", id);
        } else {
            logger.warn("No tag deleted for id: {}", id);
        }
        return deleted;
    }
}
