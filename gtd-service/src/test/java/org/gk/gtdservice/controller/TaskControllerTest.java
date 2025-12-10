package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.dto.TaskDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.service.TaskService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    private TaskDto testTaskDto;
    private CreateTaskDto createTaskDto;

    @BeforeEach
    void setUp() {
        testTaskDto = new TaskDto(
                1L,
                1L,
                2L,
                3L,
                "Buy running shoes",
                "Check sports store",
                "inbox",
                1,
                3,
                30,
                Instant.now().plusSeconds(86400),
                null,
                null,
                null,
                Instant.now(),
                null,
                null
        );
        
        createTaskDto = new CreateTaskDto(
                1L,
                2L,
                3L,
                "Buy running shoes",
                "Check sports store",
                "inbox",
                1,
                3,
                30,
                Instant.now().plusSeconds(86400),
                null,
                null,
                null,
                null
        );
    }

    @Test
    void list_AllTasks_ShouldReturnAllTasks() {
        when(taskService.findAll()).thenReturn(List.of(testTaskDto));

        List<TaskDto> result = taskController.list(null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTaskDto.title(), result.get(0).title());
        verify(taskService).findAll();
    }

    @Test
    void list_TasksByUserId_ShouldReturnTasksForUser() {
        when(taskService.findByUserId(1L)).thenReturn(List.of(testTaskDto));

        List<TaskDto> result = taskController.list(1L, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTaskDto.title(), result.get(0).title());
        verify(taskService).findByUserId(1L);
    }

    @Test
    void list_TasksByProjectId_ShouldReturnTasksForProject() {
        when(taskService.findByProjectId(2L)).thenReturn(List.of(testTaskDto));

        List<TaskDto> result = taskController.list(null, 2L, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTaskDto.title(), result.get(0).title());
        verify(taskService).findByProjectId(2L);
    }

    @Test
    void list_TasksByContextId_ShouldReturnTasksForContext() {
        when(taskService.findByContextId(3L)).thenReturn(List.of(testTaskDto));

        List<TaskDto> result = taskController.list(null, null, 3L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTaskDto.title(), result.get(0).title());
        verify(taskService).findByContextId(3L);
    }

    @Test
    void list_TasksByStatus_ShouldReturnTasksWithStatus() {
        when(taskService.findByStatus("inbox")).thenReturn(List.of(testTaskDto));

        List<TaskDto> result = taskController.list(null, null, null, "inbox");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTaskDto.title(), result.get(0).title());
        verify(taskService).findByStatus("inbox");
    }

    @Test
    void get_ExistingTask_ShouldReturnTask() {
        when(taskService.findById(1L)).thenReturn(testTaskDto);

        TaskDto result = taskController.get(1L);

        assertNotNull(result);
        assertEquals(testTaskDto.title(), result.title());
        verify(taskService).findById(1L);
    }

    @Test
    void get_NonExistingTask_ShouldThrowException() {
        when(taskService.findById(1L)).thenThrow(new ResourceNotFoundException("Task not found"));

        assertThrows(ResourceNotFoundException.class, () -> taskController.get(1L));
        verify(taskService).findById(1L);
    }

    @Test
    void create_NewTask_ShouldReturnCreated() {
        when(taskService.create(any(CreateTaskDto.class))).thenReturn(testTaskDto);

        ResponseEntity<TaskDto> response = taskController.create(createTaskDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTaskDto.title(), response.getBody().title());
        verify(taskService).create(any(CreateTaskDto.class));
    }

    @Test
    void create_WithoutProjectAndContext_ShouldReturnCreated() {
        CreateTaskDto dtoWithoutRefs = new CreateTaskDto(
                1L,
                null,
                null,
                "Simple task",
                null,
                "inbox",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        TaskDto taskDtoWithoutRefs = new TaskDto(
                1L, 1L, null, null, "Simple task", null, "inbox", null, null, null, 
                null, null, null, null, Instant.now(), null, null
        );
        when(taskService.create(any(CreateTaskDto.class))).thenReturn(taskDtoWithoutRefs);

        ResponseEntity<TaskDto> response = taskController.create(dtoWithoutRefs);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(taskService).create(any(CreateTaskDto.class));
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(taskService.create(any(CreateTaskDto.class))).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> taskController.create(createTaskDto));
        verify(taskService).create(any(CreateTaskDto.class));
    }

    @Test
    void create_NonExistingProject_ShouldThrowException() {
        when(taskService.create(any(CreateTaskDto.class))).thenThrow(new ResourceNotFoundException("Project not found"));

        assertThrows(ResourceNotFoundException.class, () -> taskController.create(createTaskDto));
        verify(taskService).create(any(CreateTaskDto.class));
    }

    @Test
    void create_NonExistingContext_ShouldThrowException() {
        when(taskService.create(any(CreateTaskDto.class))).thenThrow(new ResourceNotFoundException("Context not found"));

        assertThrows(ResourceNotFoundException.class, () -> taskController.create(createTaskDto));
        verify(taskService).create(any(CreateTaskDto.class));
    }

    @Test
    void update_ExistingTask_ShouldReturnUpdated() {
        when(taskService.update(eq(1L), any(CreateTaskDto.class))).thenReturn(testTaskDto);

        TaskDto result = taskController.update(1L, createTaskDto);

        assertNotNull(result);
        assertEquals(testTaskDto.title(), result.title());
        verify(taskService).update(eq(1L), any(CreateTaskDto.class));
    }

    @Test
    void update_NonExistingTask_ShouldThrowException() {
        when(taskService.update(eq(1L), any(CreateTaskDto.class))).thenThrow(new ResourceNotFoundException("Task not found"));

        assertThrows(ResourceNotFoundException.class, () -> taskController.update(1L, createTaskDto));
        verify(taskService).update(eq(1L), any(CreateTaskDto.class));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(taskService.update(eq(1L), any(CreateTaskDto.class))).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> taskController.update(1L, createTaskDto));
        verify(taskService).update(eq(1L), any(CreateTaskDto.class));
    }

    @Test
    void delete_ExistingTask_ShouldReturnNoContent() {
        doNothing().when(taskService).delete(1L);

        ResponseEntity<Void> response = taskController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).delete(1L);
    }

    @Test
    void delete_NonExistingTask_ShouldThrowException() {
        doThrow(new ResourceNotFoundException("Task not found")).when(taskService).delete(1L);

        assertThrows(ResourceNotFoundException.class, () -> taskController.delete(1L));
        verify(taskService).delete(1L);
    }

    @Test
    void addTag_ShouldReturnOk() {
        doNothing().when(taskService).addTagToTask(1L, 1L, 10L);

        ResponseEntity<Void> response = taskController.addTag(1L, 10L, 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(taskService).addTagToTask(1L, 1L, 10L);
    }

    @Test
    void removeTag_ShouldReturnNoContent() {
        doNothing().when(taskService).removeTagFromTask(1L, 1L, 10L);

        ResponseEntity<Void> response = taskController.removeTag(1L, 10L, 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(taskService).removeTagFromTask(1L, 1L, 10L);
    }

    @Test
    void getTags_ShouldReturnTags() {
        TagDto tagDto = new TagDto(10L, 1L, "Tag", Instant.now());
        when(taskService.getTagsForTask(1L, 1L)).thenReturn(List.of(tagDto));

        List<TagDto> result = taskController.getTags(1L, 1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(tagDto.name(), result.get(0).name());
        verify(taskService).getTagsForTask(1L, 1L);
    }
}
