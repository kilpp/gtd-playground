package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Task;
import org.gk.gtdservice.model.TaskDependency;
import org.gk.gtdservice.repo.TaskDependencyRepository;
import org.gk.gtdservice.repo.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskDependencyServiceImplTest {

    @Mock
    private TaskDependencyRepository repository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskDependencyServiceImpl service;

    private TaskDependency testDependency;
    private CreateTaskDependencyDto createDependencyDto;
    private Task task1;
    private Task task2;

    @BeforeEach
    void setUp() {
        task1 = new Task(1L, 1L, 1L, 1L, "Task 1", "Desc 1", "inbox", 1, 1, 1, Instant.now(), null, null, null, Instant.now(), null, null);
        task2 = new Task(2L, 1L, 1L, 1L, "Task 2", "Desc 2", "inbox", 1, 1, 1, Instant.now(), null, null, null, Instant.now(), null, null);
        testDependency = new TaskDependency(1L, 2L);
        createDependencyDto = new CreateTaskDependencyDto(1L, 2L);
    }

    @Test
    void findAll_ShouldReturnAllDependencies() {
        when(repository.findAll()).thenReturn(List.of(testDependency));

        List<TaskDependencyDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependency.taskId(), result.get(0).taskId());
    }

    @Test
    void findByTaskId_ShouldReturnDependenciesForTask() {
        when(repository.findByTaskId(1L)).thenReturn(List.of(testDependency));

        List<TaskDependencyDto> result = service.findByTaskId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependency.taskId(), result.get(0).taskId());
    }

    @Test
    void findByDependsOnTaskId_ShouldReturnDependenciesForDependsOnTask() {
        when(repository.findByDependsOnTaskId(2L)).thenReturn(List.of(testDependency));

        List<TaskDependencyDto> result = service.findByDependsOnTaskId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependency.taskId(), result.get(0).taskId());
    }

    @Test
    void findById_ExistingDependency_ShouldReturnDependency() {
        when(repository.findById(1L, 2L)).thenReturn(Optional.of(testDependency));

        TaskDependencyDto result = service.findById(1L, 2L);

        assertNotNull(result);
        assertEquals(testDependency.taskId(), result.taskId());
    }

    @Test
    void findById_NonExistingDependency_ShouldThrowException() {
        when(repository.findById(1L, 2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L, 2L));
    }

    @Test
    void create_ValidDependency_ShouldReturnCreated() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.findById(2L)).thenReturn(Optional.of(task2));
        when(repository.create(any())).thenReturn(testDependency);

        TaskDependencyDto result = service.create(createDependencyDto);

        assertNotNull(result);
        assertEquals(testDependency.taskId(), result.taskId());
    }

    @Test
    void create_NonExistingTask_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createDependencyDto));
    }

    @Test
    void create_NonExistingDependsOnTask_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createDependencyDto));
    }

    @Test
    void create_SelfDependency_ShouldThrowException() {
        CreateTaskDependencyDto selfDto = new CreateTaskDependencyDto(1L, 1L);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task1));

        assertThrows(IllegalArgumentException.class, () -> service.create(selfDto));
    }

    @Test
    void delete_ExistingDependency_ShouldDelete() {
        when(repository.delete(1L, 2L)).thenReturn(true);

        service.delete(1L, 2L);

        verify(repository).delete(1L, 2L);
    }

    @Test
    void delete_NonExistingDependency_ShouldThrowException() {
        when(repository.delete(1L, 2L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L, 2L));
    }

    @Test
    void deleteByTaskId_ShouldReturnCount() {
        when(repository.deleteByTaskId(1L)).thenReturn(5);

        int count = service.deleteByTaskId(1L);

        assertEquals(5, count);
        verify(repository).deleteByTaskId(1L);
    }
}
