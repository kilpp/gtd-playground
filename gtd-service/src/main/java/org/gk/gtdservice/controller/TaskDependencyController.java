package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.TaskDependencyMapper;
import org.gk.gtdservice.model.TaskDependency;
import org.gk.gtdservice.repo.TaskDependencyRepository;
import org.gk.gtdservice.repo.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/task-dependencies")
public class TaskDependencyController {

    private static final Logger logger = LoggerFactory.getLogger(TaskDependencyController.class);

    private final TaskDependencyRepository repository;
    private final TaskRepository taskRepository;

    public TaskDependencyController(TaskDependencyRepository repository, TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public List<TaskDependencyDto> list(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long dependsOnTaskId
    ) {
        logger.info("Listing task dependencies with filters - taskId: {}, dependsOnTaskId: {}", taskId, dependsOnTaskId);
        List<TaskDependency> dependencies;
        
        if (taskId != null) {
            dependencies = repository.findByTaskId(taskId);
        } else if (dependsOnTaskId != null) {
            dependencies = repository.findByDependsOnTaskId(dependsOnTaskId);
        } else {
            dependencies = repository.findAll();
        }
        
        logger.debug("Retrieved {} task dependencies", dependencies.size());
        return dependencies.stream().map(TaskDependencyMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{taskId}/{dependsOnTaskId}")
    public TaskDependencyDto get(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId) {
        logger.info("Getting task dependency: taskId={}, dependsOnTaskId={}", taskId, dependsOnTaskId);
        return repository.findById(taskId, dependsOnTaskId)
                .map(TaskDependencyMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Task dependency not found"));
    }

    @PostMapping
    public ResponseEntity<TaskDependencyDto> create(@Valid @RequestBody CreateTaskDependencyDto dto) {
        logger.info("Creating task dependency: {}", dto);
        
        // Ensure both tasks exist
        if (taskRepository.findById(dto.taskId()).isEmpty()) {
            logger.warn("Task not found for taskId: {}", dto.taskId());
            throw new ResourceNotFoundException("Task not found with id: " + dto.taskId());
        }
        
        if (taskRepository.findById(dto.dependsOnTaskId()).isEmpty()) {
            logger.warn("Task not found for dependsOnTaskId: {}", dto.dependsOnTaskId());
            throw new ResourceNotFoundException("Task not found with id: " + dto.dependsOnTaskId());
        }
        
        // Prevent self-dependency
        if (dto.taskId().equals(dto.dependsOnTaskId())) {
            logger.warn("Attempted to create self-dependency for taskId: {}", dto.taskId());
            throw new IllegalArgumentException("A task cannot depend on itself");
        }
        
        var saved = repository.create(dto);
        TaskDependencyDto out = TaskDependencyMapper.toDto(saved);
        logger.info("Created task dependency: taskId={}, dependsOnTaskId={}", saved.taskId(), saved.dependsOnTaskId());
        return ResponseEntity.created(
                URI.create("/api/task-dependencies/" + saved.taskId() + "/" + saved.dependsOnTaskId())
        ).body(out);
    }

    @DeleteMapping("/{taskId}/{dependsOnTaskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId) {
        logger.info("Deleting task dependency: taskId={}, dependsOnTaskId={}", taskId, dependsOnTaskId);
        boolean deleted = repository.delete(taskId, dependsOnTaskId);
        if (!deleted) {
            logger.warn("Task dependency not found for deletion");
            throw new ResourceNotFoundException("Task dependency not found");
        }
        logger.info("Deleted task dependency");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Void> deleteByTaskId(@PathVariable Long taskId) {
        logger.info("Deleting all dependencies for taskId: {}", taskId);
        int deleted = repository.deleteByTaskId(taskId);
        logger.info("Deleted {} dependencies for taskId: {}", deleted, taskId);
        return ResponseEntity.noContent().build();
    }
}
