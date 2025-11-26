package org.gk.gtdservice.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskDependencyControllerTest {

    @Mock
    private TaskDependencyRepository dependencyRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskDependencyController controller;

    private TaskDependency testDependency;
    private CreateTaskDependencyDto createDependencyDto;
    private Task testTask1;
    private Task testTask2;

    @BeforeEach
    void setUp() {
        testTask1 = new Task(1L, 1L, null, null, "Task 1", null, "inbox", null, null, null,
                            null, null, null, null, Instant.now(), null, null);
        testTask2 = new Task(2L, 1L, null, null, "Task 2", null, "inbox", null, null, null,
                            null, null, null, null, Instant.now(), null, null);
        testDependency = new TaskDependency(2L, 1L);
        createDependencyDto = new CreateTaskDependencyDto(2L, 1L);
    }

    @Test
    void list_AllDependencies_ShouldReturnAllDependencies() {
        when(dependencyRepository.findAll()).thenReturn(List.of(testDependency));

        List<TaskDependencyDto> result = controller.list(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependency.taskId(), result.get(0).taskId());
    }

    @Test
    void list_DependenciesByTaskId_ShouldReturnDependenciesForTask() {
        when(dependencyRepository.findByTaskId(2L)).thenReturn(List.of(testDependency));

        List<TaskDependencyDto> result = controller.list(2L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependency.taskId(), result.get(0).taskId());
    }

    @Test
    void list_DependenciesByDependsOnTaskId_ShouldReturnTasksDependingOn() {
        when(dependencyRepository.findByDependsOnTaskId(1L)).thenReturn(List.of(testDependency));

        List<TaskDependencyDto> result = controller.list(null, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependency.dependsOnTaskId(), result.get(0).dependsOnTaskId());
    }

    @Test
    void get_ExistingDependency_ShouldReturnDependency() {
        when(dependencyRepository.findById(2L, 1L)).thenReturn(Optional.of(testDependency));

        TaskDependencyDto result = controller.get(2L, 1L);

        assertNotNull(result);
        assertEquals(testDependency.taskId(), result.taskId());
        assertEquals(testDependency.dependsOnTaskId(), result.dependsOnTaskId());
    }

    @Test
    void get_NonExistingDependency_ShouldThrowException() {
        when(dependencyRepository.findById(2L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controller.get(2L, 1L));
    }

    @Test
    void create_NewDependency_ShouldReturnCreated() {
        when(taskRepository.findById(2L)).thenReturn(Optional.of(testTask2));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask1));
        when(dependencyRepository.create(any())).thenReturn(testDependency);

        ResponseEntity<TaskDependencyDto> response = controller.create(createDependencyDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testDependency.taskId(), response.getBody().taskId());
    }

    @Test
    void create_NonExistingTask_ShouldThrowException() {
        when(taskRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controller.create(createDependencyDto));
    }

    @Test
    void create_NonExistingDependsOnTask_ShouldThrowException() {
        when(taskRepository.findById(2L)).thenReturn(Optional.of(testTask2));
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> controller.create(createDependencyDto));
    }

    @Test
    void create_SelfDependency_ShouldThrowException() {
        CreateTaskDependencyDto selfDep = new CreateTaskDependencyDto(1L, 1L);

        assertThrows(IllegalArgumentException.class, () -> controller.create(selfDep));
    }

    @Test
    void delete_ExistingDependency_ShouldReturnNoContent() {
        when(dependencyRepository.delete(2L, 1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(2L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingDependency_ShouldThrowException() {
        when(dependencyRepository.delete(2L, 1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> controller.delete(2L, 1L));
    }

    @Test
    void deleteByTaskId_ShouldReturnNoContent() {
        when(dependencyRepository.deleteByTaskId(1L)).thenReturn(2);

        ResponseEntity<Void> response = controller.deleteByTaskId(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
