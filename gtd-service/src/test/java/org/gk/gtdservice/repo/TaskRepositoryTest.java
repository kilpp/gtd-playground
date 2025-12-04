package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Area;
import org.gk.gtdservice.model.Context;
import org.gk.gtdservice.model.Project;
import org.gk.gtdservice.model.Task;
import org.gk.gtdservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({TaskRepository.class, UserRepository.class, ProjectRepository.class, ContextRepository.class, AreaRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private AreaRepository areaRepository;

    private CreateTaskDto createTaskDto;
    private User testUser;
    private Project testProject;
    private Context testContext;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);
        
        CreateAreaDto createAreaDto = new CreateAreaDto(testUser.id(), "Work", "Work area");
        Area testArea = areaRepository.create(createAreaDto);
        
        CreateProjectDto createProjectDto = new CreateProjectDto(
                testUser.id(),
                testArea.id(),
                "Test Project",
                "Test outcome",
                null,
                "active",
                LocalDate.of(2025, 12, 31)
        );
        testProject = projectRepository.create(createProjectDto);
        
        CreateContextDto createContextDto = new CreateContextDto(testUser.id(), "Office", "Office context", false);
        testContext = contextRepository.create(createContextDto);
        
        createTaskDto = new CreateTaskDto(
                testUser.id(),
                testProject.id(),
                testContext.id(),
                "Buy new running shoes",
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
    void create_ShouldInsertTask() {
        Task task = taskRepository.create(createTaskDto);

        assertNotNull(task);
        assertNotNull(task.id());
        assertEquals(createTaskDto.userId(), task.userId());
        assertEquals(createTaskDto.projectId(), task.projectId());
        assertEquals(createTaskDto.contextId(), task.contextId());
        assertEquals(createTaskDto.title(), task.title());
        assertEquals(createTaskDto.notes(), task.notes());
        assertEquals(createTaskDto.status(), task.status());
        assertEquals(createTaskDto.priority(), task.priority());
        assertEquals(createTaskDto.energy(), task.energy());
        assertEquals(createTaskDto.durationEstMin(), task.durationEstMin());
        assertNotNull(task.createdAt());
        assertNull(task.completedAt());
    }

    @Test
    void create_WithMinimalFields_ShouldInsertTask() {
        CreateTaskDto dto = new CreateTaskDto(
                testUser.id(),
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

        Task task = taskRepository.create(dto);

        assertNotNull(task);
        assertNull(task.projectId());
        assertNull(task.contextId());
        assertNull(task.priority());
    }

    @Test
    void findById_ExistingTask_ShouldReturnTask() {
        Task created = taskRepository.create(createTaskDto);

        Optional<Task> found = taskRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals(created.title(), found.get().title());
    }

    @Test
    void findById_NonExistingTask_ShouldReturnEmpty() {
        Optional<Task> found = taskRepository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllTasks() {
        Task task1 = taskRepository.create(createTaskDto);
        CreateTaskDto dto2 = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Another task",
                null,
                "next",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Task task2 = taskRepository.create(dto2);

        List<Task> tasks = taskRepository.findAll();

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task1.id())));
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task2.id())));
    }

    @Test
    void findByUserId_ShouldReturnTasksForUser() {
        Task task1 = taskRepository.create(createTaskDto);
        CreateTaskDto dto2 = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Another task",
                null,
                "next",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Task task2 = taskRepository.create(dto2);
        
        CreateUserDto createUserDto2 = new CreateUserDto("testuser2", "test2@example.com", "Test User 2");
        User testUser2 = userRepository.create(createUserDto2);
        CreateTaskDto dto3 = new CreateTaskDto(
                testUser2.id(),
                null,
                null,
                "Other user task",
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
        taskRepository.create(dto3);

        List<Task> tasks = taskRepository.findByUserId(testUser.id());

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task1.id())));
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task2.id())));
    }

    @Test
    void findByProjectId_ShouldReturnTasksForProject() {
        Task task1 = taskRepository.create(createTaskDto);
        CreateTaskDto dto2 = new CreateTaskDto(
                testUser.id(),
                testProject.id(),
                null,
                "Another project task",
                null,
                "next",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Task task2 = taskRepository.create(dto2);
        
        CreateTaskDto dto3 = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Task without project",
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
        taskRepository.create(dto3);

        List<Task> tasks = taskRepository.findByProjectId(testProject.id());

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task1.id())));
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task2.id())));
    }

    @Test
    void findByContextId_ShouldReturnTasksForContext() {
        Task task1 = taskRepository.create(createTaskDto);
        CreateTaskDto dto2 = new CreateTaskDto(
                testUser.id(),
                null,
                testContext.id(),
                "Another context task",
                null,
                "next",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Task task2 = taskRepository.create(dto2);
        
        CreateTaskDto dto3 = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Task without context",
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
        taskRepository.create(dto3);

        List<Task> tasks = taskRepository.findByContextId(testContext.id());

        assertEquals(2, tasks.size());
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task1.id())));
        assertTrue(tasks.stream().anyMatch(t -> t.id().equals(task2.id())));
    }

    @Test
    void findByStatus_ShouldReturnTasksWithStatus() {
        Task task1 = taskRepository.create(createTaskDto);
        CreateTaskDto dto2 = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Next action task",
                null,
                "next",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
        Task task2 = taskRepository.create(dto2);

        List<Task> inboxTasks = taskRepository.findByStatus("inbox");
        List<Task> nextTasks = taskRepository.findByStatus("next");

        assertEquals(1, inboxTasks.size());
        assertEquals(task1.id(), inboxTasks.get(0).id());
        assertEquals(1, nextTasks.size());
        assertEquals(task2.id(), nextTasks.get(0).id());
    }

    @Test
    void update_ExistingTask_ShouldUpdateAndReturnTask() {
        Task created = taskRepository.create(createTaskDto);
        CreateTaskDto updateDto = new CreateTaskDto(
                testUser.id(),
                testProject.id(),
                testContext.id(),
                "Updated title",
                "Updated notes",
                "next",
                2,
                4,
                45,
                Instant.now().plusSeconds(172800),
                null,
                null,
                null,
                100
        );

        Task updated = taskRepository.update(created.id(), updateDto);

        assertNotNull(updated);
        assertEquals(created.id(), updated.id());
        assertEquals(updateDto.title(), updated.title());
        assertEquals(updateDto.notes(), updated.notes());
        assertEquals(updateDto.status(), updated.status());
        assertEquals(updateDto.priority(), updated.priority());
        assertEquals(updateDto.energy(), updated.energy());
        assertEquals(updateDto.durationEstMin(), updated.durationEstMin());
        assertEquals(updateDto.orderIndex(), updated.orderIndex());
    }

    @Test
    void update_StatusToDone_ShouldUpdateStatus() {
        Task created = taskRepository.create(createTaskDto);
        CreateTaskDto updateDto = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Completed task",
                "Done",
                "done",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );

        Task updated = taskRepository.update(created.id(), updateDto);

        assertNotNull(updated);
        assertEquals("done", updated.status());
        assertEquals("Completed task", updated.title());
    }

    @Test
    void update_NonExistingTask_ShouldReturnNull() {
        CreateTaskDto updateDto = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Task",
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

        Task updated = taskRepository.update(999L, updateDto);

        assertNull(updated);
    }

    @Test
    void delete_ExistingTask_ShouldReturnTrue() {
        Task created = taskRepository.create(createTaskDto);

        boolean deleted = taskRepository.delete(created.id());

        assertTrue(deleted);
        assertTrue(taskRepository.findById(created.id()).isEmpty());
    }

    @Test
    void delete_NonExistingTask_ShouldReturnFalse() {
        boolean deleted = taskRepository.delete(999L);

        assertFalse(deleted);
    }
}
