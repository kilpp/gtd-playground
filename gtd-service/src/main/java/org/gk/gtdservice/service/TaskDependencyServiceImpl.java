package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.TaskDependencyMapper;
import org.gk.gtdservice.model.TaskDependency;
import org.gk.gtdservice.repo.TaskDependencyRepository;
import org.gk.gtdservice.repo.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskDependencyServiceImpl implements TaskDependencyService {

    private static final Logger logger = LoggerFactory.getLogger(TaskDependencyServiceImpl.class);

    private final TaskDependencyRepository repository;
    private final TaskRepository taskRepository;

    public TaskDependencyServiceImpl(TaskDependencyRepository repository, TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependencyDto> findAll() {
        logger.info("Listing all task dependencies");
        return repository.findAll().stream()
                .map(TaskDependencyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependencyDto> findByTaskId(Long taskId) {
        logger.info("Listing dependencies for taskId: {}", taskId);
        return repository.findByTaskId(taskId).stream()
                .map(TaskDependencyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDependencyDto> findByDependsOnTaskId(Long dependsOnTaskId) {
        logger.info("Listing dependencies for dependsOnTaskId: {}", dependsOnTaskId);
        return repository.findByDependsOnTaskId(dependsOnTaskId).stream()
                .map(TaskDependencyMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDependencyDto findById(Long taskId, Long dependsOnTaskId) {
        logger.info("Getting task dependency: taskId={}, dependsOnTaskId={}", taskId, dependsOnTaskId);
        return repository.findById(taskId, dependsOnTaskId)
                .map(TaskDependencyMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Task dependency not found"));
    }

    @Override
    public TaskDependencyDto create(CreateTaskDependencyDto dto) {
        logger.info("Creating task dependency: {}", dto);
        
        if (taskRepository.findById(dto.taskId()).isEmpty()) {
            throw new ResourceNotFoundException("Task not found with id: " + dto.taskId());
        }
        
        if (taskRepository.findById(dto.dependsOnTaskId()).isEmpty()) {
            throw new ResourceNotFoundException("Task not found with id: " + dto.dependsOnTaskId());
        }
        
        if (dto.taskId().equals(dto.dependsOnTaskId())) {
            throw new IllegalArgumentException("A task cannot depend on itself");
        }
        
        TaskDependency saved = repository.create(dto);
        return TaskDependencyMapper.toDto(saved);
    }

    @Override
    public void delete(Long taskId, Long dependsOnTaskId) {
        logger.info("Deleting task dependency: taskId={}, dependsOnTaskId={}", taskId, dependsOnTaskId);
        if (!repository.delete(taskId, dependsOnTaskId)) {
            throw new ResourceNotFoundException("Task dependency not found");
        }
    }

    @Override
    public int deleteByTaskId(Long taskId) {
        logger.info("Deleting all dependencies for taskId: {}", taskId);
        int deleted = repository.deleteByTaskId(taskId);
        logger.info("Deleted {} dependencies for taskId: {}", deleted, taskId);
        return deleted;
    }
}
