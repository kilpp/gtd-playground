package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TaskDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Context;
import org.gk.gtdservice.model.Project;
import org.gk.gtdservice.model.Task;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.ContextRepository;
import org.gk.gtdservice.repo.ProjectRepository;
import org.gk.gtdservice.repo.TaskRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ContextRepository contextRepository;

    @InjectMocks
    private TaskController taskController;

    private Task testTask;
    private CreateTaskDto createTaskDto;
    private User testUser;
    private Project testProject;
    private Context testContext;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testProject = new Project(2L, 1L, null, "Test Project", "Outcome", null, "active", 
                                  LocalDate.of(2025, 12, 31), Instant.now(), null);
        testContext = new Context(3L, 1L, "Office", "Office context", false, Instant.now());
        
        testTask = new Task(
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
        when(taskRepository.findAll()).thenReturn(List.of(testTask));

        List<TaskDto> result = taskController.list(null, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
    }

    @Test
    void list_TasksByUserId_ShouldReturnTasksForUser() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(testTask));

        List<TaskDto> result = taskController.list(1L, null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
    }

    @Test
    void list_TasksByProjectId_ShouldReturnTasksForProject() {
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of(testTask));

        List<TaskDto> result = taskController.list(null, 2L, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
    }

    @Test
    void list_TasksByContextId_ShouldReturnTasksForContext() {
        when(taskRepository.findByContextId(3L)).thenReturn(List.of(testTask));

        List<TaskDto> result = taskController.list(null, null, 3L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
    }

    @Test
    void list_TasksByStatus_ShouldReturnTasksWithStatus() {
        when(taskRepository.findByStatus("inbox")).thenReturn(List.of(testTask));

        List<TaskDto> result = taskController.list(null, null, null, "inbox");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
    }

    @Test
    void get_ExistingTask_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        TaskDto result = taskController.get(1L);

        assertNotNull(result);
        assertEquals(testTask.title(), result.title());
    }

    @Test
    void get_NonExistingTask_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskController.get(1L));
    }

    @Test
    void create_NewTask_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.of(testContext));
        when(taskRepository.create(any())).thenReturn(testTask);

        ResponseEntity<TaskDto> response = taskController.create(createTaskDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTask.title(), response.getBody().title());
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
        Task taskWithoutRefs = new Task(
                1L, 1L, null, null, "Simple task", null, "inbox", null, null, null, 
                null, null, null, null, Instant.now(), null, null
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.create(any())).thenReturn(taskWithoutRefs);

        ResponseEntity<TaskDto> response = taskController.create(dtoWithoutRefs);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskController.create(createTaskDto));
    }

    @Test
    void create_NonExistingProject_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskController.create(createTaskDto));
    }

    @Test
    void create_NonExistingContext_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskController.create(createTaskDto));
    }

    @Test
    void update_ExistingTask_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.of(testContext));
        when(taskRepository.update(1L, createTaskDto)).thenReturn(testTask);

        TaskDto result = taskController.update(1L, createTaskDto);

        assertNotNull(result);
        assertEquals(testTask.title(), result.title());
    }

    @Test
    void update_NonExistingTask_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.of(testContext));
        when(taskRepository.update(1L, createTaskDto)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> taskController.update(1L, createTaskDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskController.update(1L, createTaskDto));
    }

    @Test
    void delete_ExistingTask_ShouldReturnNoContent() {
        when(taskRepository.delete(1L)).thenReturn(true);

        ResponseEntity<Void> response = taskController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingTask_ShouldThrowException() {
        when(taskRepository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskController.delete(1L));
    }
}
