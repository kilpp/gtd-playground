package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProjectRepository {

    private static final Logger logger = LoggerFactory.getLogger(ProjectRepository.class);

    private final NamedParameterJdbcTemplate jdbc;

    public ProjectRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Project> mapper = (rs, _rowNum) -> {
        var areaId = rs.getObject("area_id", Long.class);
        var dueDate = rs.getDate("due_date");
        var completedAt = rs.getTimestamp("completed_at");
        return new Project(
                rs.getLong("id"),
                rs.getLong("user_id"),
                areaId,
                rs.getString("title"),
                rs.getString("outcome"),
                rs.getString("notes"),
                rs.getString("status"),
                dueDate != null ? dueDate.toLocalDate() : null,
                rs.getTimestamp("created_at").toInstant(),
                completedAt != null ? completedAt.toInstant() : null
        );
    };

    public List<Project> findAll() {
        logger.info("Finding all projects");
        List<Project> projects = jdbc.query(
                "SELECT id, user_id, area_id, title, outcome, notes, status, due_date, created_at, completed_at FROM gtd.projects",
                Collections.emptyMap(),
                mapper
        );
        logger.debug("Found {} projects", projects.size());
        return projects;
    }

    public List<Project> findByUserId(Long userId) {
        logger.info("Finding projects by userId: {}", userId);
        List<Project> projects = jdbc.query(
                "SELECT id, user_id, area_id, title, outcome, notes, status, due_date, created_at, completed_at FROM gtd.projects WHERE user_id = :user_id",
                Map.of("user_id", userId),
                mapper
        );
        logger.debug("Found {} projects for userId: {}", projects.size(), userId);
        return projects;
    }

    public List<Project> findByAreaId(Long areaId) {
        logger.info("Finding projects by areaId: {}", areaId);
        List<Project> projects = jdbc.query(
                "SELECT id, user_id, area_id, title, outcome, notes, status, due_date, created_at, completed_at FROM gtd.projects WHERE area_id = :area_id",
                Map.of("area_id", areaId),
                mapper
        );
        logger.debug("Found {} projects for areaId: {}", projects.size(), areaId);
        return projects;
    }

    public List<Project> findByStatus(String status) {
        logger.info("Finding projects by status: {}", status);
        List<Project> projects = jdbc.query(
                "SELECT id, user_id, area_id, title, outcome, notes, status, due_date, created_at, completed_at FROM gtd.projects WHERE status = :status",
                Map.of("status", status),
                mapper
        );
        logger.debug("Found {} projects with status: {}", projects.size(), status);
        return projects;
    }

    public Optional<Project> findById(Long id) {
        logger.info("Finding project by id: {}", id);
        Map<String, Object> params = Map.of("id", id);
        List<Project> l = jdbc.query(
                "SELECT id, user_id, area_id, title, outcome, notes, status, due_date, created_at, completed_at FROM gtd.projects WHERE id = :id",
                params,
                mapper
        );
        Optional<Project> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found project with id: {}", id);
        } else {
            logger.debug("Project not found with id: {}", id);
        }
        return result;
    }

    public Project create(CreateProjectDto dto) throws DataIntegrityViolationException {
        logger.info("Creating project: {}", dto);
        String sql = "INSERT INTO gtd.projects (user_id, area_id, title, outcome, notes, status, due_date, created_at) " +
                     "VALUES (:user_id, :area_id, :title, :outcome, :notes, :status, :due_date, :created_at)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("area_id", dto.areaId())
                .addValue("title", dto.title())
                .addValue("outcome", dto.outcome())
                .addValue("notes", dto.notes())
                .addValue("status", dto.status())
                .addValue("due_date", dto.dueDate() != null ? Date.valueOf(dto.dueDate()) : null)
                .addValue("created_at", Timestamp.from(Instant.now()));
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        logger.info("Created project with id: {}", id);
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created project"));
    }

    public Project update(Long id, CreateProjectDto dto) {
        logger.info("Updating project with id: {}, dto: {}", id, dto);
        
        // Determine if we need to update completed_at
        Optional<Project> existing = findById(id);
        Instant completedAt = null;
        if (existing.isPresent()) {
            Project currentProject = existing.get();
            // If status is changing to 'completed', set completed_at
            if ("completed".equals(dto.status()) && !"completed".equals(currentProject.status())) {
                completedAt = Instant.now();
            }
            // If status is changing from 'completed' to something else, clear completed_at
            else if (!"completed".equals(dto.status()) && "completed".equals(currentProject.status())) {
                completedAt = null; // Will be handled by SQL
            }
            // If already completed and staying completed, keep the old timestamp
            else if ("completed".equals(dto.status()) && "completed".equals(currentProject.status())) {
                completedAt = currentProject.completedAt();
            }
        }

        String sql = "UPDATE gtd.projects SET user_id = :user_id, area_id = :area_id, title = :title, " +
                     "outcome = :outcome, notes = :notes, status = :status, due_date = :due_date, " +
                     "completed_at = :completed_at WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("user_id", dto.userId())
                .addValue("area_id", dto.areaId())
                .addValue("title", dto.title())
                .addValue("outcome", dto.outcome())
                .addValue("notes", dto.notes())
                .addValue("status", dto.status())
                .addValue("due_date", dto.dueDate() != null ? Date.valueOf(dto.dueDate()) : null)
                .addValue("completed_at", completedAt != null ? Timestamp.from(completedAt) : null)
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            logger.warn("No project updated for id: {}", id);
            return null;
        }
        logger.info("Updated project with id: {}", id);
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        logger.info("Deleting project with id: {}", id);
        int updated = jdbc.update("DELETE FROM gtd.projects WHERE id = :id", Map.of("id", id));
        boolean deleted = updated > 0;
        if (deleted) {
            logger.info("Deleted project with id: {}", id);
        } else {
            logger.warn("No project deleted for id: {}", id);
        }
        return deleted;
    }
}
