package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Task;
import org.gk.gtdservice.model.TaskDependency;
import org.gk.gtdservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({TaskDependencyRepository.class, TaskRepository.class, UserRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class TaskDependencyRepositoryTest {

    @Autowired
    private TaskDependencyRepository dependencyRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private CreateTaskDependencyDto createDependencyDto;
    private Task testTask1;
    private Task testTask2;
    private User testUser;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);
        
        CreateTaskDto taskDto1 = new CreateTaskDto(
                testUser.id(), null, null, "Task 1", null, "inbox", null, null, null, null, null, null, null, null
        );
        testTask1 = taskRepository.create(taskDto1);
        
        CreateTaskDto taskDto2 = new CreateTaskDto(
                testUser.id(), null, null, "Task 2", null, "inbox", null, null, null, null, null, null, null, null
        );
        testTask2 = taskRepository.create(taskDto2);
        
        createDependencyDto = new CreateTaskDependencyDto(testTask2.id(), testTask1.id());
    }

    @Test
    void create_ShouldInsertDependency() {
        TaskDependency dependency = dependencyRepository.create(createDependencyDto);

        assertNotNull(dependency);
        assertEquals(createDependencyDto.taskId(), dependency.taskId());
        assertEquals(createDependencyDto.dependsOnTaskId(), dependency.dependsOnTaskId());
    }

    @Test
    void create_SelfDependency_ShouldThrowException() {
        CreateTaskDependencyDto selfDep = new CreateTaskDependencyDto(testTask1.id(), testTask1.id());

        assertThrows(DataIntegrityViolationException.class, () -> dependencyRepository.create(selfDep));
    }

    @Test
    void create_NonExistingTask_ShouldThrowException() {
        CreateTaskDependencyDto invalidDto = new CreateTaskDependencyDto(999L, testTask1.id());

        assertThrows(DataIntegrityViolationException.class, () -> dependencyRepository.create(invalidDto));
    }

    @Test
    void findById_ExistingDependency_ShouldReturnDependency() {
        TaskDependency created = dependencyRepository.create(createDependencyDto);

        Optional<TaskDependency> found = dependencyRepository.findById(created.taskId(), created.dependsOnTaskId());

        assertTrue(found.isPresent());
        assertEquals(created.taskId(), found.get().taskId());
        assertEquals(created.dependsOnTaskId(), found.get().dependsOnTaskId());
    }

    @Test
    void findById_NonExistingDependency_ShouldReturnEmpty() {
        Optional<TaskDependency> found = dependencyRepository.findById(testTask1.id(), testTask2.id());

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllDependencies() {
        TaskDependency dep1 = dependencyRepository.create(createDependencyDto);
        
        CreateTaskDto taskDto3 = new CreateTaskDto(
                testUser.id(), null, null, "Task 3", null, "inbox", null, null, null, null, null, null, null, null
        );
        Task testTask3 = taskRepository.create(taskDto3);
        CreateTaskDependencyDto dto2 = new CreateTaskDependencyDto(testTask3.id(), testTask2.id());
        TaskDependency dep2 = dependencyRepository.create(dto2);

        List<TaskDependency> dependencies = dependencyRepository.findAll();

        assertEquals(2, dependencies.size());
        assertTrue(dependencies.stream().anyMatch(d -> d.taskId().equals(dep1.taskId()) && d.dependsOnTaskId().equals(dep1.dependsOnTaskId())));
        assertTrue(dependencies.stream().anyMatch(d -> d.taskId().equals(dep2.taskId()) && d.dependsOnTaskId().equals(dep2.dependsOnTaskId())));
    }

    @Test
    void findByTaskId_ShouldReturnDependenciesForTask() {
        dependencyRepository.create(createDependencyDto);
        
        CreateTaskDto taskDto3 = new CreateTaskDto(
                testUser.id(), null, null, "Task 3", null, "inbox", null, null, null, null, null, null, null, null
        );
        Task testTask3 = taskRepository.create(taskDto3);
        CreateTaskDependencyDto dto2 = new CreateTaskDependencyDto(testTask2.id(), testTask3.id());
        dependencyRepository.create(dto2);

        List<TaskDependency> dependencies = dependencyRepository.findByTaskId(testTask2.id());

        assertEquals(2, dependencies.size());
        assertTrue(dependencies.stream().allMatch(d -> d.taskId().equals(testTask2.id())));
    }

    @Test
    void findByDependsOnTaskId_ShouldReturnTasksDependingOn() {
        dependencyRepository.create(createDependencyDto);
        
        CreateTaskDto taskDto3 = new CreateTaskDto(
                testUser.id(), null, null, "Task 3", null, "inbox", null, null, null, null, null, null, null, null
        );
        Task testTask3 = taskRepository.create(taskDto3);
        CreateTaskDependencyDto dto2 = new CreateTaskDependencyDto(testTask3.id(), testTask1.id());
        dependencyRepository.create(dto2);

        List<TaskDependency> dependencies = dependencyRepository.findByDependsOnTaskId(testTask1.id());

        assertEquals(2, dependencies.size());
        assertTrue(dependencies.stream().allMatch(d -> d.dependsOnTaskId().equals(testTask1.id())));
    }

    @Test
    void delete_ExistingDependency_ShouldReturnTrue() {
        TaskDependency created = dependencyRepository.create(createDependencyDto);

        boolean deleted = dependencyRepository.delete(created.taskId(), created.dependsOnTaskId());

        assertTrue(deleted);
        assertTrue(dependencyRepository.findById(created.taskId(), created.dependsOnTaskId()).isEmpty());
    }

    @Test
    void delete_NonExistingDependency_ShouldReturnFalse() {
        boolean deleted = dependencyRepository.delete(testTask1.id(), testTask2.id());

        assertFalse(deleted);
    }

    @Test
    void deleteByTaskId_ShouldDeleteAllRelatedDependencies() {
        dependencyRepository.create(createDependencyDto);
        
        CreateTaskDto taskDto3 = new CreateTaskDto(
                testUser.id(), null, null, "Task 3", null, "inbox", null, null, null, null, null, null, null, null
        );
        Task testTask3 = taskRepository.create(taskDto3);
        CreateTaskDependencyDto dto2 = new CreateTaskDependencyDto(testTask3.id(), testTask1.id());
        dependencyRepository.create(dto2);

        int deleted = dependencyRepository.deleteByTaskId(testTask1.id());

        assertEquals(2, deleted);
        assertTrue(dependencyRepository.findByTaskId(testTask1.id()).isEmpty());
        assertTrue(dependencyRepository.findByDependsOnTaskId(testTask1.id()).isEmpty());
    }

    @Test
    void deleteTask_ShouldCascadeDeleteDependencies() {
        dependencyRepository.create(createDependencyDto);

        taskRepository.delete(testTask1.id());

        List<TaskDependency> remaining = dependencyRepository.findAll();
        assertEquals(0, remaining.size());
    }
}
