package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.dto.TaskDto;
import org.gk.gtdservice.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<TaskDto> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long projectId,
            @RequestParam(required = false) Long contextId,
            @RequestParam(required = false) String status
    ) {
        logger.info("Listing tasks with filters - userId: {}, projectId: {}, contextId: {}, status: {}", 
                   userId, projectId, contextId, status);
        
        if (userId != null) {
            return taskService.findByUserId(userId);
        } else if (projectId != null) {
            return taskService.findByProjectId(projectId);
        } else if (contextId != null) {
            return taskService.findByContextId(contextId);
        } else if (status != null) {
            return taskService.findByStatus(status);
        } else {
            return taskService.findAll();
        }
    }

    @GetMapping("/{id}")
    public TaskDto get(@PathVariable Long id) {
        logger.info("Getting task with id: {}", id);
        return taskService.findById(id);
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody CreateTaskDto dto) {
        logger.info("Creating task: {}", dto);
        TaskDto created = taskService.create(dto);
        logger.info("Created task with id: {}", created.id());
        return ResponseEntity.created(URI.create("/api/tasks/" + created.id())).body(created);
    }

    @PutMapping("/{id}")
    public TaskDto update(@PathVariable Long id, @Valid @RequestBody CreateTaskDto dto) {
        logger.info("Updating task with id: {}, dto: {}", id, dto);
        TaskDto updated = taskService.update(id, dto);
        logger.info("Updated task with id: {}", id);
        return updated;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting task with id: {}", id);
        taskService.delete(id);
        logger.info("Deleted task with id: {}", id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<Void> addTag(@PathVariable Long taskId, @PathVariable Long tagId, @RequestParam Long userId) {
        logger.info("Adding tag {} to task {} for user {}", tagId, taskId, userId);
        taskService.addTagToTask(userId, taskId, tagId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{taskId}/tags/{tagId}")
    public ResponseEntity<Void> removeTag(@PathVariable Long taskId, @PathVariable Long tagId, @RequestParam Long userId) {
        logger.info("Removing tag {} from task {} for user {}", tagId, taskId, userId);
        taskService.removeTagFromTask(userId, taskId, tagId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{taskId}/tags")
    public List<TagDto> getTags(@PathVariable Long taskId, @RequestParam Long userId) {
        logger.info("Getting tags for task {} for user {}", taskId, userId);
        return taskService.getTagsForTask(userId, taskId);
    }
}
