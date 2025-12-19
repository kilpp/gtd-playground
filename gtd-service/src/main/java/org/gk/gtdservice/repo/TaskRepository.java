package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.model.Task;
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
public class TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepository.class);

    private final NamedParameterJdbcTemplate jdbc;

    public TaskRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Task> mapper = (rs, _rowNum) -> {
        var projectId = rs.getObject("project_id", Long.class);
        var contextId = rs.getObject("context_id", Long.class);
        var priority = rs.getObject("priority", Integer.class);
        var energy = rs.getObject("energy", Integer.class);
        var durationEstMin = rs.getObject("duration_est_min", Integer.class);
        var dueAt = rs.getTimestamp("due_at");
        var deferUntil = rs.getTimestamp("defer_until");
        var waitingSince = rs.getTimestamp("waiting_since");
        var completedAt = rs.getTimestamp("completed_at");
        var orderIndex = rs.getObject("order_index", Integer.class);
        
        return new Task(
                rs.getLong("id"),
                rs.getLong("user_id"),
                projectId,
                contextId,
                rs.getString("title"),
                rs.getString("notes"),
                rs.getString("status"),
                priority,
                energy,
                durationEstMin,
                dueAt != null ? dueAt.toInstant() : null,
                deferUntil != null ? deferUntil.toInstant() : null,
                rs.getString("waiting_on"),
                waitingSince != null ? waitingSince.toInstant() : null,
                rs.getTimestamp("created_at").toInstant(),
                completedAt != null ? completedAt.toInstant() : null,
                orderIndex
        );
    };

    public List<Task> findAll() {
        logger.info("Finding all tasks");
        List<Task> tasks = jdbc.query(
                "SELECT id, user_id, project_id, context_id, title, notes, status, priority, energy, " +
                "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, completed_at, order_index " +
                "FROM gtd.tasks ORDER BY order_index, created_at",
                Collections.emptyMap(),
                mapper
        );
        logger.debug("Found {} tasks", tasks.size());
        return tasks;
    }

    public List<Task> findByUserId(Long userId) {
        logger.info("Finding tasks by userId: {}", userId);
        List<Task> tasks = jdbc.query(
                "SELECT id, user_id, project_id, context_id, title, notes, status, priority, energy, " +
                "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, completed_at, order_index " +
                "FROM gtd.tasks WHERE user_id = :user_id ORDER BY order_index, created_at",
                Map.of("user_id", userId),
                mapper
        );
        logger.debug("Found {} tasks for userId: {}", tasks.size(), userId);
        return tasks;
    }

    public List<Task> findByProjectId(Long projectId) {
        logger.info("Finding tasks by projectId: {}", projectId);
        List<Task> tasks = jdbc.query(
                "SELECT id, user_id, project_id, context_id, title, notes, status, priority, energy, " +
                "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, completed_at, order_index " +
                "FROM gtd.tasks WHERE project_id = :project_id ORDER BY order_index, created_at",
                Map.of("project_id", projectId),
                mapper
        );
        logger.debug("Found {} tasks for projectId: {}", tasks.size(), projectId);
        return tasks;
    }

    public List<Task> findByContextId(Long contextId) {
        logger.info("Finding tasks by contextId: {}", contextId);
        List<Task> tasks = jdbc.query(
                "SELECT id, user_id, project_id, context_id, title, notes, status, priority, energy, " +
                "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, completed_at, order_index " +
                "FROM gtd.tasks WHERE context_id = :context_id ORDER BY order_index, created_at",
                Map.of("context_id", contextId),
                mapper
        );
        logger.debug("Found {} tasks for contextId: {}", tasks.size(), contextId);
        return tasks;
    }

    public List<Task> findByStatus(String status) {
        logger.info("Finding tasks by status: {}", status);
        List<Task> tasks = jdbc.query(
                "SELECT id, user_id, project_id, context_id, title, notes, status, priority, energy, " +
                "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, completed_at, order_index " +
                "FROM gtd.tasks WHERE status = :status ORDER BY order_index, created_at",
                Map.of("status", status),
                mapper
        );
        logger.debug("Found {} tasks with status: {}", tasks.size(), status);
        return tasks;
    }

    public List<Task> findByUserIdAndStatus(Long userId, String status) {
        logger.info("Finding tasks by userId: {} and status: {}", userId, status);
        List<Task> tasks = jdbc.query(
                "SELECT id, user_id, project_id, context_id, title, notes, status, priority, energy, " +
                "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, completed_at, order_index " +
                "FROM gtd.tasks WHERE user_id = :user_id AND status = :status ORDER BY order_index, created_at",
                Map.of("user_id", userId, "status", status),
                mapper
        );
        logger.debug("Found {} tasks for userId: {} with status: {}", tasks.size(), userId, status);
        return tasks;
    }

    public Optional<Task> findById(Long id) {
        logger.info("Finding task by id: {}", id);
        Map<String, Object> params = Map.of("id", id);
        List<Task> l = jdbc.query(
                "SELECT id, user_id, project_id, context_id, title, notes, status, priority, energy, " +
                "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, completed_at, order_index " +
                "FROM gtd.tasks WHERE id = :id",
                params,
                mapper
        );
        Optional<Task> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found task with id: {}", id);
        } else {
            logger.debug("Task not found with id: {}", id);
        }
        return result;
    }

    public Task create(CreateTaskDto dto) throws DataIntegrityViolationException {
        logger.info("Creating task: {}", dto);
        String sql = "INSERT INTO gtd.tasks (user_id, project_id, context_id, title, notes, status, priority, energy, " +
                     "duration_est_min, due_at, defer_until, waiting_on, waiting_since, created_at, order_index) " +
                     "VALUES (:user_id, :project_id, :context_id, :title, :notes, :status, :priority, :energy, " +
                     ":duration_est_min, :due_at, :defer_until, :waiting_on, :waiting_since, :created_at, :order_index)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("project_id", dto.projectId())
                .addValue("context_id", dto.contextId())
                .addValue("title", dto.title())
                .addValue("notes", dto.notes())
                .addValue("status", dto.status())
                .addValue("priority", dto.priority())
                .addValue("energy", dto.energy())
                .addValue("duration_est_min", dto.durationEstMin())
                .addValue("due_at", dto.dueAt() != null ? Timestamp.from(dto.dueAt()) : null)
                .addValue("defer_until", dto.deferUntil() != null ? Timestamp.from(dto.deferUntil()) : null)
                .addValue("waiting_on", dto.waitingOn())
                .addValue("waiting_since", dto.waitingSince() != null ? Timestamp.from(dto.waitingSince()) : null)
                .addValue("created_at", Timestamp.from(Instant.now()))
                .addValue("order_index", dto.orderIndex());
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        logger.info("Created task with id: {}", id);
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created task"));
    }

    public Task update(Long id, CreateTaskDto dto) {
        logger.info("Updating task with id: {}, dto: {}", id, dto);
        String sql = "UPDATE gtd.tasks SET user_id = :user_id, project_id = :project_id, context_id = :context_id, " +
                     "title = :title, notes = :notes, status = :status, priority = :priority, energy = :energy, " +
                     "duration_est_min = :duration_est_min, due_at = :due_at, defer_until = :defer_until, " +
                     "waiting_on = :waiting_on, waiting_since = :waiting_since, order_index = :order_index WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("project_id", dto.projectId())
                .addValue("context_id", dto.contextId())
                .addValue("title", dto.title())
                .addValue("notes", dto.notes())
                .addValue("status", dto.status())
                .addValue("priority", dto.priority())
                .addValue("energy", dto.energy())
                .addValue("duration_est_min", dto.durationEstMin())
                .addValue("due_at", dto.dueAt() != null ? Timestamp.from(dto.dueAt()) : null)
                .addValue("defer_until", dto.deferUntil() != null ? Timestamp.from(dto.deferUntil()) : null)
                .addValue("waiting_on", dto.waitingOn())
                .addValue("waiting_since", dto.waitingSince() != null ? Timestamp.from(dto.waitingSince()) : null)
                .addValue("order_index", dto.orderIndex())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            logger.warn("No task updated for id: {}", id);
            return null;
        }
        logger.info("Updated task with id: {}", id);
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        logger.info("Deleting task with id: {}", id);
        int updated = jdbc.update("DELETE FROM gtd.tasks WHERE id = :id", Map.of("id", id));
        boolean deleted = updated > 0;
        if (deleted) {
            logger.info("Deleted task with id: {}", id);
        } else {
            logger.warn("No task deleted for id: {}", id);
        }
        return deleted;
    }
}
