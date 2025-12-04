package org.gk.gtdservice.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ContextRepository contextRepository;

    public TaskServiceImpl(TaskRepository taskRepository, UserRepository userRepository,
                           ProjectRepository projectRepository, ContextRepository contextRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.contextRepository = contextRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findAll() {
        logger.info("Finding all tasks");
        List<Task> tasks = taskRepository.findAll();
        logger.debug("Found {} tasks", tasks.size());
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findByUserId(Long userId) {
        logger.info("Finding tasks by userId: {}", userId);
        List<Task> tasks = taskRepository.findByUserId(userId);
        logger.debug("Found {} tasks for userId: {}", tasks.size(), userId);
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findByProjectId(Long projectId) {
        logger.info("Finding tasks by projectId: {}", projectId);
        List<Task> tasks = taskRepository.findByProjectId(projectId);
        logger.debug("Found {} tasks for projectId: {}", tasks.size(), projectId);
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findByContextId(Long contextId) {
        logger.info("Finding tasks by contextId: {}", contextId);
        List<Task> tasks = taskRepository.findByContextId(contextId);
        logger.debug("Found {} tasks for contextId: {}", tasks.size(), contextId);
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDto> findByStatus(String status) {
        logger.info("Finding tasks by status: {}", status);
        List<Task> tasks = taskRepository.findByStatus(status);
        logger.debug("Found {} tasks with status: {}", tasks.size(), status);
        return tasks.stream().map(TaskMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto findById(Long id) {
        logger.info("Finding task by id: {}", id);
        return taskRepository.findById(id)
                .map(TaskMapper::toDto)
                .orElseThrow(() -> {
                    logger.warn("Task not found with id: {}", id);
                    return new ResourceNotFoundException("Task not found");
                });
    }

    @Override
    public TaskDto create(CreateTaskDto dto) {
        logger.info("Creating task: {}", dto);
        
        // Validate user exists
        validateUserExists(dto.userId());
        
        // Validate project exists if provided
        if (dto.projectId() != null) {
            validateProjectExists(dto.projectId());
        }
        
        // Validate context exists if provided
        if (dto.contextId() != null) {
            validateContextExists(dto.contextId());
        }
        
        Task saved = taskRepository.create(dto);
        logger.info("Created task with id: {}", saved.id());
        return TaskMapper.toDto(saved);
    }

    @Override
    public TaskDto update(Long id, CreateTaskDto dto) {
        logger.info("Updating task with id: {}, dto: {}", id, dto);
        
        // Validate user exists
        validateUserExists(dto.userId());
        
        // Validate project exists if provided
        if (dto.projectId() != null) {
            validateProjectExists(dto.projectId());
        }
        
        // Validate context exists if provided
        if (dto.contextId() != null) {
            validateContextExists(dto.contextId());
        }
        
        Task saved = taskRepository.update(id, dto);
        if (saved == null) {
            logger.warn("Task not found for id: {}", id);
            throw new ResourceNotFoundException("Task not found");
        }
        
        logger.info("Updated task with id: {}", id);
        return TaskMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting task with id: {}", id);
        boolean deleted = taskRepository.delete(id);
        if (!deleted) {
            logger.warn("Task not found for deletion, id: {}", id);
            throw new ResourceNotFoundException("Task not found");
        }
        logger.info("Deleted task with id: {}", id);
    }

    private void validateUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            logger.warn("User not found for userId: {}", userId);
            throw new ResourceNotFoundException("User not found");
        }
    }

    private void validateProjectExists(Long projectId) {
        if (projectRepository.findById(projectId).isEmpty()) {
            logger.warn("Project not found for projectId: {}", projectId);
            throw new ResourceNotFoundException("Project not found");
        }
    }

    private void validateContextExists(Long contextId) {
        if (contextRepository.findById(contextId).isEmpty()) {
            logger.warn("Context not found for contextId: {}", contextId);
            throw new ResourceNotFoundException("Context not found");
        }
    }
}
