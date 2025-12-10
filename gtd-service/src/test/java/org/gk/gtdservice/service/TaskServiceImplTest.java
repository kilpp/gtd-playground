package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TaskDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Context;
import org.gk.gtdservice.model.Project;
import org.gk.gtdservice.model.Task;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.ContextRepository;
import org.gk.gtdservice.repo.ProjectRepository;
import org.gk.gtdservice.repo.TagRepository;
import org.gk.gtdservice.repo.TaskRepository;
import org.gk.gtdservice.repo.TaskTagRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ContextRepository contextRepository;

    @Mock
    private TaskTagRepository taskTagRepository;

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

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

    // FindAll tests
    @Test
    void findAll_ShouldReturnAllTasks() {
        when(taskRepository.findAll()).thenReturn(List.of(testTask));

        List<TaskDto> result = taskService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
        verify(taskRepository).findAll();
    }

    @Test
    void findAll_EmptyList_ShouldReturnEmptyList() {
        when(taskRepository.findAll()).thenReturn(List.of());

        List<TaskDto> result = taskService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(taskRepository).findAll();
    }

    // FindByUserId tests
    @Test
    void findByUserId_ShouldReturnTasksForUser() {
        when(taskRepository.findByUserId(1L)).thenReturn(List.of(testTask));

        List<TaskDto> result = taskService.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
        verify(taskRepository).findByUserId(1L);
    }

    // FindByProjectId tests
    @Test
    void findByProjectId_ShouldReturnTasksForProject() {
        when(taskRepository.findByProjectId(2L)).thenReturn(List.of(testTask));

        List<TaskDto> result = taskService.findByProjectId(2L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
        verify(taskRepository).findByProjectId(2L);
    }

    // FindByContextId tests
    @Test
    void findByContextId_ShouldReturnTasksForContext() {
        when(taskRepository.findByContextId(3L)).thenReturn(List.of(testTask));

        List<TaskDto> result = taskService.findByContextId(3L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
        verify(taskRepository).findByContextId(3L);
    }

    // FindByStatus tests
    @Test
    void findByStatus_ShouldReturnTasksWithStatus() {
        when(taskRepository.findByStatus("inbox")).thenReturn(List.of(testTask));

        List<TaskDto> result = taskService.findByStatus("inbox");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.title(), result.get(0).title());
        verify(taskRepository).findByStatus("inbox");
    }

    // FindById tests
    @Test
    void findById_ExistingTask_ShouldReturnTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        TaskDto result = taskService.findById(1L);

        assertNotNull(result);
        assertEquals(testTask.title(), result.title());
        assertEquals(testTask.id(), result.id());
        verify(taskRepository).findById(1L);
    }

    @Test
    void findById_NonExistingTask_ShouldThrowException() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.findById(999L));
        verify(taskRepository).findById(999L);
    }

    // Create tests
    @Test
    void create_WithAllValidReferences_ShouldCreateTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.of(testContext));
        when(taskRepository.create(any(CreateTaskDto.class))).thenReturn(testTask);

        TaskDto result = taskService.create(createTaskDto);

        assertNotNull(result);
        assertEquals(testTask.title(), result.title());
        verify(userRepository).findById(1L);
        verify(projectRepository).findById(2L);
        verify(contextRepository).findById(3L);
        verify(taskRepository).create(any(CreateTaskDto.class));
    }

    @Test
    void create_WithoutProjectAndContext_ShouldCreateTask() {
        CreateTaskDto dtoWithoutRefs = new CreateTaskDto(
                1L, null, null, "Simple task", null, "inbox",
                null, null, null, null, null, null, null, null
        );
        Task taskWithoutRefs = new Task(
                1L, 1L, null, null, "Simple task", null, "inbox", null, null, null,
                null, null, null, null, Instant.now(), null, null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.create(any(CreateTaskDto.class))).thenReturn(taskWithoutRefs);

        TaskDto result = taskService.create(dtoWithoutRefs);

        assertNotNull(result);
        assertEquals("Simple task", result.title());
        verify(userRepository).findById(1L);
        verify(projectRepository, never()).findById(any());
        verify(contextRepository, never()).findById(any());
        verify(taskRepository).create(any(CreateTaskDto.class));
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(createTaskDto));
        verify(userRepository).findById(1L);
        verify(taskRepository, never()).create(any());
    }

    @Test
    void create_NonExistingProject_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(createTaskDto));
        verify(userRepository).findById(1L);
        verify(projectRepository).findById(2L);
        verify(taskRepository, never()).create(any());
    }

    @Test
    void create_NonExistingContext_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.create(createTaskDto));
        verify(userRepository).findById(1L);
        verify(projectRepository).findById(2L);
        verify(contextRepository).findById(3L);
        verify(taskRepository, never()).create(any());
    }

    @Test
    void create_WithOnlyProject_ShouldCreateTask() {
        CreateTaskDto dtoWithProject = new CreateTaskDto(
                1L, 2L, null, "Task with project", null, "inbox",
                null, null, null, null, null, null, null, null
        );
        Task taskWithProject = new Task(
                1L, 1L, 2L, null, "Task with project", null, "inbox", null, null, null,
                null, null, null, null, Instant.now(), null, null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(taskRepository.create(any(CreateTaskDto.class))).thenReturn(taskWithProject);

        TaskDto result = taskService.create(dtoWithProject);

        assertNotNull(result);
        assertEquals("Task with project", result.title());
        verify(userRepository).findById(1L);
        verify(projectRepository).findById(2L);
        verify(contextRepository, never()).findById(any());
        verify(taskRepository).create(any(CreateTaskDto.class));
    }

    // Update tests
    @Test
    void update_ExistingTask_ShouldUpdateTask() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.of(testContext));
        when(taskRepository.update(eq(1L), any(CreateTaskDto.class))).thenReturn(testTask);

        TaskDto result = taskService.update(1L, createTaskDto);

        assertNotNull(result);
        assertEquals(testTask.title(), result.title());
        verify(userRepository).findById(1L);
        verify(projectRepository).findById(2L);
        verify(contextRepository).findById(3L);
        verify(taskRepository).update(eq(1L), any(CreateTaskDto.class));
    }

    @Test
    void update_NonExistingTask_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.of(testContext));
        when(taskRepository.update(eq(999L), any(CreateTaskDto.class))).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> taskService.update(999L, createTaskDto));
        verify(taskRepository).update(eq(999L), any(CreateTaskDto.class));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.update(1L, createTaskDto));
        verify(userRepository).findById(1L);
        verify(taskRepository, never()).update(any(), any());
    }

    @Test
    void update_NonExistingProject_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.update(1L, createTaskDto));
        verify(userRepository).findById(1L);
        verify(projectRepository).findById(2L);
        verify(taskRepository, never()).update(any(), any());
    }

    @Test
    void update_NonExistingContext_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.findById(2L)).thenReturn(Optional.of(testProject));
        when(contextRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.update(1L, createTaskDto));
        verify(userRepository).findById(1L);
        verify(projectRepository).findById(2L);
        verify(contextRepository).findById(3L);
        verify(taskRepository, never()).update(any(), any());
    }

    @Test
    void update_RemovingProjectAndContext_ShouldUpdateTask() {
        CreateTaskDto dtoWithoutRefs = new CreateTaskDto(
                1L, null, null, "Updated task", null, "inbox",
                null, null, null, null, null, null, null, null
        );
        Task updatedTask = new Task(
                1L, 1L, null, null, "Updated task", null, "inbox", null, null, null,
                null, null, null, null, Instant.now(), null, null
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(taskRepository.update(eq(1L), any(CreateTaskDto.class))).thenReturn(updatedTask);

        TaskDto result = taskService.update(1L, dtoWithoutRefs);

        assertNotNull(result);
        assertEquals("Updated task", result.title());
        assertNull(result.projectId());
        assertNull(result.contextId());
        verify(userRepository).findById(1L);
        verify(projectRepository, never()).findById(any());
        verify(contextRepository, never()).findById(any());
        verify(taskRepository).update(eq(1L), any(CreateTaskDto.class));
    }

    // Delete tests
    @Test
    void delete_ExistingTask_ShouldDeleteTask() {
        when(taskRepository.delete(1L)).thenReturn(true);

        assertDoesNotThrow(() -> taskService.delete(1L));
        verify(taskRepository).delete(1L);
    }

    @Test
    void delete_NonExistingTask_ShouldThrowException() {
        when(taskRepository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> taskService.delete(1L));
        verify(taskRepository).delete(1L);
    }

    // Tag tests
    @Test
    void addTagToTask_ShouldAddTag() {
        org.gk.gtdservice.model.Tag tag = new org.gk.gtdservice.model.Tag(10L, 1L, "Tag", Instant.now());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(tagRepository.findById(10L)).thenReturn(Optional.of(tag));

        taskService.addTagToTask(1L, 1L, 10L);

        verify(taskTagRepository).addTagToTask(1L, 10L);
    }

    @Test
    void addTagToTask_TaskNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.addTagToTask(1L, 1L, 10L));
    }

    @Test
    void addTagToTask_TagNotFound_ShouldThrowException() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(tagRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskService.addTagToTask(1L, 1L, 10L));
    }

    @Test
    void removeTagFromTask_ShouldRemoveTag() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        taskService.removeTagFromTask(1L, 1L, 10L);

        verify(taskTagRepository).removeTagFromTask(1L, 10L);
    }

    @Test
    void getTagsForTask_ShouldReturnTags() {
        org.gk.gtdservice.model.Tag tag = new org.gk.gtdservice.model.Tag(10L, 1L, "Tag", Instant.now());
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskTagRepository.findTagsByTaskId(1L)).thenReturn(List.of(tag));

        List<org.gk.gtdservice.dto.TagDto> tags = taskService.getTagsForTask(1L, 1L);

        assertNotNull(tags);
        assertEquals(1, tags.size());
        assertEquals(tag.id(), tags.get(0).id());
    }
}
