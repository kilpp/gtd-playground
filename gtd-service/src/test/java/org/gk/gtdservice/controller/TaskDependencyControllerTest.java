package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.service.TaskDependencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskDependencyControllerTest {

    @Mock
    private TaskDependencyService service;

    @InjectMocks
    private TaskDependencyController controller;

    private TaskDependencyDto testDependencyDto;
    private CreateTaskDependencyDto createDependencyDto;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        testDependencyDto = new TaskDependencyDto(2L, 1L);
        createDependencyDto = new CreateTaskDependencyDto(2L, 1L);
    }

    @Test
    void list_AllDependencies_ShouldReturnAllDependencies() {
        when(service.findAll()).thenReturn(List.of(testDependencyDto));

        List<TaskDependencyDto> result = controller.list(null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependencyDto.taskId(), result.get(0).taskId());
    }

    @Test
    void list_DependenciesByTaskId_ShouldReturnDependenciesForTask() {
        when(service.findByTaskId(2L)).thenReturn(List.of(testDependencyDto));

        List<TaskDependencyDto> result = controller.list(2L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependencyDto.taskId(), result.get(0).taskId());
    }

    @Test
    void list_DependenciesByDependsOnTaskId_ShouldReturnTasksDependingOn() {
        when(service.findByDependsOnTaskId(1L)).thenReturn(List.of(testDependencyDto));

        List<TaskDependencyDto> result = controller.list(null, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testDependencyDto.dependsOnTaskId(), result.get(0).dependsOnTaskId());
    }

    @Test
    void get_ExistingDependency_ShouldReturnDependency() {
        when(service.findById(2L, 1L)).thenReturn(testDependencyDto);

        TaskDependencyDto result = controller.get(2L, 1L);

        assertNotNull(result);
        assertEquals(testDependencyDto.taskId(), result.taskId());
        assertEquals(testDependencyDto.dependsOnTaskId(), result.dependsOnTaskId());
    }

    @Test
    void get_NonExistingDependency_ShouldThrowException() {
        when(service.findById(2L, 1L)).thenThrow(new ResourceNotFoundException("Task dependency not found"));

        assertThrows(ResourceNotFoundException.class, () -> controller.get(2L, 1L));
    }

    @Test
    void create_NewDependency_ShouldReturnCreated() {
        when(service.create(any())).thenReturn(testDependencyDto);

        ResponseEntity<TaskDependencyDto> response = controller.create(createDependencyDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testDependencyDto.taskId(), response.getBody().taskId());
    }

    @Test
    void create_NonExistingTask_ShouldThrowException() {
        when(service.create(any())).thenThrow(new ResourceNotFoundException("Task not found"));

        assertThrows(ResourceNotFoundException.class, () -> controller.create(createDependencyDto));
    }

    @Test
    void create_NonExistingDependsOnTask_ShouldThrowException() {
        when(service.create(any())).thenThrow(new ResourceNotFoundException("Task not found"));

        assertThrows(ResourceNotFoundException.class, () -> controller.create(createDependencyDto));
    }

    @Test
    void create_SelfDependency_ShouldThrowException() {
        CreateTaskDependencyDto selfDep = new CreateTaskDependencyDto(1L, 1L);
        when(service.create(selfDep)).thenThrow(new IllegalArgumentException("A task cannot depend on itself"));

        assertThrows(IllegalArgumentException.class, () -> controller.create(selfDep));
    }

    @Test
    void delete_ExistingDependency_ShouldReturnNoContent() {
        doNothing().when(service).delete(2L, 1L);

        ResponseEntity<Void> response = controller.delete(2L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingDependency_ShouldThrowException() {
        doThrow(new ResourceNotFoundException("Task dependency not found")).when(service).delete(2L, 1L);

        assertThrows(ResourceNotFoundException.class, () -> controller.delete(2L, 1L));
    }

    @Test
    void deleteByTaskId_ShouldReturnNoContent() {
        when(service.deleteByTaskId(1L)).thenReturn(2);

        ResponseEntity<Void> response = controller.deleteByTaskId(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
