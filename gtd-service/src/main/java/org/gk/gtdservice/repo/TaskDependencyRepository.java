package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.model.TaskDependency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TaskDependencyRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskDependencyRepository.class);

    private final NamedParameterJdbcTemplate jdbc;

    public TaskDependencyRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<TaskDependency> mapper = (rs, _rowNum) -> new TaskDependency(
            rs.getLong("task_id"),
            rs.getLong("depends_on_task_id")
    );

    public List<TaskDependency> findAll() {
        logger.info("Finding all task dependencies");
        List<TaskDependency> dependencies = jdbc.query(
                "SELECT task_id, depends_on_task_id FROM gtd.task_dependencies",
                Collections.emptyMap(),
                mapper
        );
        logger.debug("Found {} task dependencies", dependencies.size());
        return dependencies;
    }

    public List<TaskDependency> findByTaskId(Long taskId) {
        logger.info("Finding task dependencies for taskId: {}", taskId);
        List<TaskDependency> dependencies = jdbc.query(
                "SELECT task_id, depends_on_task_id FROM gtd.task_dependencies WHERE task_id = :task_id",
                Map.of("task_id", taskId),
                mapper
        );
        logger.debug("Found {} dependencies for taskId: {}", dependencies.size(), taskId);
        return dependencies;
    }

    public List<TaskDependency> findByDependsOnTaskId(Long dependsOnTaskId) {
        logger.info("Finding tasks depending on taskId: {}", dependsOnTaskId);
        List<TaskDependency> dependencies = jdbc.query(
                "SELECT task_id, depends_on_task_id FROM gtd.task_dependencies WHERE depends_on_task_id = :depends_on_task_id",
                Map.of("depends_on_task_id", dependsOnTaskId),
                mapper
        );
        logger.debug("Found {} tasks depending on taskId: {}", dependencies.size(), dependsOnTaskId);
        return dependencies;
    }

    public Optional<TaskDependency> findById(Long taskId, Long dependsOnTaskId) {
        logger.info("Finding task dependency by taskId: {} and dependsOnTaskId: {}", taskId, dependsOnTaskId);
        Map<String, Object> params = Map.of(
                "task_id", taskId,
                "depends_on_task_id", dependsOnTaskId
        );
        List<TaskDependency> l = jdbc.query(
                "SELECT task_id, depends_on_task_id FROM gtd.task_dependencies WHERE task_id = :task_id AND depends_on_task_id = :depends_on_task_id",
                params,
                mapper
        );
        Optional<TaskDependency> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found task dependency");
        } else {
            logger.debug("Task dependency not found");
        }
        return result;
    }

    public TaskDependency create(CreateTaskDependencyDto dto) throws DataIntegrityViolationException {
        logger.info("Creating task dependency: {}", dto);
        String sql = "INSERT INTO gtd.task_dependencies (task_id, depends_on_task_id) VALUES (:task_id, :depends_on_task_id)";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("task_id", dto.taskId())
                .addValue("depends_on_task_id", dto.dependsOnTaskId());
        jdbc.update(sql, params);
        logger.info("Created task dependency: taskId={}, dependsOnTaskId={}", dto.taskId(), dto.dependsOnTaskId());
        return findById(dto.taskId(), dto.dependsOnTaskId())
                .orElseThrow(() -> new RuntimeException("Failed to load created task dependency"));
    }

    public boolean delete(Long taskId, Long dependsOnTaskId) {
        logger.info("Deleting task dependency: taskId={}, dependsOnTaskId={}", taskId, dependsOnTaskId);
        Map<String, Object> params = Map.of(
                "task_id", taskId,
                "depends_on_task_id", dependsOnTaskId
        );
        int updated = jdbc.update(
                "DELETE FROM gtd.task_dependencies WHERE task_id = :task_id AND depends_on_task_id = :depends_on_task_id",
                params
        );
        boolean deleted = updated > 0;
        if (deleted) {
            logger.info("Deleted task dependency");
        } else {
            logger.warn("No task dependency deleted");
        }
        return deleted;
    }

    public int deleteByTaskId(Long taskId) {
        logger.info("Deleting all dependencies for taskId: {}", taskId);
        int updated = jdbc.update(
                "DELETE FROM gtd.task_dependencies WHERE task_id = :task_id OR depends_on_task_id = :task_id",
                Map.of("task_id", taskId)
        );
        logger.info("Deleted {} dependencies for taskId: {}", updated, taskId);
        return updated;
    }
}
