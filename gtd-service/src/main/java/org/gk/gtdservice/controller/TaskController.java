package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TaskDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.TaskMapper;
import org.gk.gtdservice.model.Task;
import org.gk.gtdservice.repo.ContextRepository;
import org.gk.gtdservice.repo.ProjectRepository;
import org.gk.gtdservice.repo.TaskRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    private final TaskRepository repository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ContextRepository contextRepository;

    public TaskController(TaskRepository repository, UserRepository userRepository, 
                         ProjectRepository projectRepository, ContextRepository contextRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.contextRepository = contextRepository;
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
        List<Task> tasks;
        
        if (userId != null) {
            tasks = repository.findByUserId(userId);
        } else if (projectId != null) {
            tasks = repository.findByProjectId(projectId);
        } else if (contextId != null) {
            tasks = repository.findByContextId(contextId);
        } else if (status != null) {
            tasks = repository.findByStatus(status);
        } else {
            tasks = repository.findAll();
        }
        
        logger.debug("Retrieved {} tasks", tasks.size());
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TaskDto get(@PathVariable Long id) {
        logger.info("Getting task with id: {}", id);
        return repository.findById(id)
                .map(TaskMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));
    }

    @PostMapping
    public ResponseEntity<TaskDto> create(@Valid @RequestBody CreateTaskDto dto) {
        logger.info("Creating task: {}", dto);
        
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        
        // ensure project exists if provided
        if (dto.projectId() != null && projectRepository.findById(dto.projectId()).isEmpty()) {
            logger.warn("Project not found for projectId: {}", dto.projectId());
            throw new ResourceNotFoundException("Project not found");
        }
        
        // ensure context exists if provided
        if (dto.contextId() != null && contextRepository.findById(dto.contextId()).isEmpty()) {
            logger.warn("Context not found for contextId: {}", dto.contextId());
            throw new ResourceNotFoundException("Context not found");
        }
        
        var saved = repository.create(dto);
        TaskDto out = TaskMapper.toDto(saved);
        logger.info("Created task with id: {}", saved.id());
        return ResponseEntity.created(URI.create("/api/tasks/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public TaskDto update(@PathVariable Long id, @Valid @RequestBody CreateTaskDto dto) {
        logger.info("Updating task with id: {}, dto: {}", id, dto);
        
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        
        // ensure project exists if provided
        if (dto.projectId() != null && projectRepository.findById(dto.projectId()).isEmpty()) {
            logger.warn("Project not found for projectId: {}", dto.projectId());
            throw new ResourceNotFoundException("Project not found");
        }
        
        // ensure context exists if provided
        if (dto.contextId() != null && contextRepository.findById(dto.contextId()).isEmpty()) {
            logger.warn("Context not found for contextId: {}", dto.contextId());
            throw new ResourceNotFoundException("Context not found");
        }
        
        Task saved = repository.update(id, dto);
        if (saved == null) {
            logger.warn("Task not found for id: {}", id);
            throw new ResourceNotFoundException("Task not found");
        }
        logger.info("Updated task with id: {}", id);
        return TaskMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting task with id: {}", id);
        boolean deleted = repository.delete(id);
        if (!deleted) {
            logger.warn("Task not found for deletion, id: {}", id);
            throw new ResourceNotFoundException("Task not found");
        }
        logger.info("Deleted task with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
