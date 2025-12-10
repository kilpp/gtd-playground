package org.gk.gtdservice.repo;

import org.gk.gtdservice.model.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Repository
public class TaskTagRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskTagRepository.class);

    private final NamedParameterJdbcTemplate jdbc;

    public TaskTagRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Tag> tagMapper = (rs, _rowNum) -> new Tag(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("name"),
            rs.getTimestamp("created_at").toInstant()
    );

    public void addTagToTask(Long taskId, Long tagId) {
        logger.info("Adding tag {} to task {}", tagId, taskId);
        String sql = "INSERT INTO gtd.task_tags (task_id, tag_id) VALUES (:task_id, :tag_id)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("task_id", taskId)
                .addValue("tag_id", tagId);
        try {
            jdbc.update(sql, params);
        } catch (Exception e) {
            // Ignore if already exists or handle appropriately
            logger.warn("Failed to add tag {} to task {}: {}", tagId, taskId, e.getMessage());
        }
    }

    public void removeTagFromTask(Long taskId, Long tagId) {
        logger.info("Removing tag {} from task {}", tagId, taskId);
        String sql = "DELETE FROM gtd.task_tags WHERE task_id = :task_id AND tag_id = :tag_id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("task_id", taskId)
                .addValue("tag_id", tagId);
        jdbc.update(sql, params);
    }

    public List<Tag> findTagsByTaskId(Long taskId) {
        logger.info("Finding tags for task {}", taskId);
        String sql = """
                SELECT t.id, t.user_id, t.name, t.created_at
                FROM gtd.tags t
                JOIN gtd.task_tags tt ON t.id = tt.tag_id
                WHERE tt.task_id = :task_id
                """;
        List<Tag> tags = jdbc.query(sql, Map.of("task_id", taskId), tagMapper);
        logger.debug("Found {} tags for task {}", tags.size(), taskId);
        return tags;
    }
}
