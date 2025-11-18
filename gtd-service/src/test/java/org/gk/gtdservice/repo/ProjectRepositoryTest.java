package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Area;
import org.gk.gtdservice.model.Project;
import org.gk.gtdservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({ProjectRepository.class, UserRepository.class, AreaRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AreaRepository areaRepository;

    private CreateProjectDto createProjectDto;
    private User testUser;
    private Area testArea;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);
        
        CreateAreaDto createAreaDto = new CreateAreaDto(testUser.id(), "Work", "Work area");
        testArea = areaRepository.create(createAreaDto);
        
        createProjectDto = new CreateProjectDto(
                testUser.id(),
                testArea.id(),
                "Redesign website",
                "Modern, responsive website",
                "Use React and Tailwind",
                "active",
                LocalDate.of(2025, 12, 31)
        );
    }

    @Test
    void create_ShouldInsertProject() {
        Project project = projectRepository.create(createProjectDto);

        assertNotNull(project);
        assertNotNull(project.id());
        assertEquals(createProjectDto.userId(), project.userId());
        assertEquals(createProjectDto.areaId(), project.areaId());
        assertEquals(createProjectDto.title(), project.title());
        assertEquals(createProjectDto.outcome(), project.outcome());
        assertEquals(createProjectDto.notes(), project.notes());
        assertEquals(createProjectDto.status(), project.status());
        assertEquals(createProjectDto.dueDate(), project.dueDate());
        assertNotNull(project.createdAt());
        assertNull(project.completedAt());
    }

    @Test
    void create_WithoutArea_ShouldInsertProject() {
        CreateProjectDto dto = new CreateProjectDto(
                testUser.id(),
                null,
                "Personal goal",
                "Achieve something",
                null,
                "active",
                null
        );

        Project project = projectRepository.create(dto);

        assertNotNull(project);
        assertNull(project.areaId());
    }

    @Test
    void findById_ExistingProject_ShouldReturnProject() {
        Project created = projectRepository.create(createProjectDto);

        Optional<Project> found = projectRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals(created.title(), found.get().title());
    }

    @Test
    void findById_NonExistingProject_ShouldReturnEmpty() {
        Optional<Project> found = projectRepository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllProjects() {
        Project project1 = projectRepository.create(createProjectDto);
        CreateProjectDto dto2 = new CreateProjectDto(
                testUser.id(),
                testArea.id(),
                "Another project",
                "Another outcome",
                null,
                "someday",
                null
        );
        Project project2 = projectRepository.create(dto2);

        List<Project> projects = projectRepository.findAll();

        assertEquals(2, projects.size());
        assertTrue(projects.stream().anyMatch(p -> p.id().equals(project1.id())));
        assertTrue(projects.stream().anyMatch(p -> p.id().equals(project2.id())));
    }

    @Test
    void findByUserId_ShouldReturnProjectsForUser() {
        Project project1 = projectRepository.create(createProjectDto);
        CreateProjectDto dto2 = new CreateProjectDto(
                testUser.id(),
                null,
                "Another project",
                "Another outcome",
                null,
                "active",
                null
        );
        Project project2 = projectRepository.create(dto2);
        
        CreateUserDto createUserDto2 = new CreateUserDto("testuser2", "test2@example.com", "Test User 2");
        User testUser2 = userRepository.create(createUserDto2);
        CreateProjectDto dto3 = new CreateProjectDto(
                testUser2.id(),
                null,
                "Other user project",
                null,
                null,
                "active",
                null
        );
        projectRepository.create(dto3);

        List<Project> projects = projectRepository.findByUserId(testUser.id());

        assertEquals(2, projects.size());
        assertTrue(projects.stream().anyMatch(p -> p.id().equals(project1.id())));
        assertTrue(projects.stream().anyMatch(p -> p.id().equals(project2.id())));
    }

    @Test
    void findByAreaId_ShouldReturnProjectsForArea() {
        Project project1 = projectRepository.create(createProjectDto);
        CreateProjectDto dto2 = new CreateProjectDto(
                testUser.id(),
                testArea.id(),
                "Another project",
                null,
                null,
                "active",
                null
        );
        Project project2 = projectRepository.create(dto2);
        
        CreateProjectDto dto3 = new CreateProjectDto(
                testUser.id(),
                null,
                "Project without area",
                null,
                null,
                "active",
                null
        );
        projectRepository.create(dto3);

        List<Project> projects = projectRepository.findByAreaId(testArea.id());

        assertEquals(2, projects.size());
        assertTrue(projects.stream().anyMatch(p -> p.id().equals(project1.id())));
        assertTrue(projects.stream().anyMatch(p -> p.id().equals(project2.id())));
    }

    @Test
    void findByStatus_ShouldReturnProjectsWithStatus() {
        Project project1 = projectRepository.create(createProjectDto);
        CreateProjectDto dto2 = new CreateProjectDto(
                testUser.id(),
                null,
                "Someday project",
                null,
                null,
                "someday",
                null
        );
        Project project2 = projectRepository.create(dto2);

        List<Project> activeProjects = projectRepository.findByStatus("active");
        List<Project> somedayProjects = projectRepository.findByStatus("someday");

        assertEquals(1, activeProjects.size());
        assertEquals(project1.id(), activeProjects.get(0).id());
        assertEquals(1, somedayProjects.size());
        assertEquals(project2.id(), somedayProjects.get(0).id());
    }

    @Test
    void update_ExistingProject_ShouldUpdateAndReturnProject() {
        Project created = projectRepository.create(createProjectDto);
        CreateProjectDto updateDto = new CreateProjectDto(
                testUser.id(),
                testArea.id(),
                "Updated title",
                "Updated outcome",
                "Updated notes",
                "on_hold",
                LocalDate.of(2026, 6, 30)
        );

        Project updated = projectRepository.update(created.id(), updateDto);

        assertNotNull(updated);
        assertEquals(created.id(), updated.id());
        assertEquals(updateDto.title(), updated.title());
        assertEquals(updateDto.outcome(), updated.outcome());
        assertEquals(updateDto.notes(), updated.notes());
        assertEquals(updateDto.status(), updated.status());
        assertEquals(updateDto.dueDate(), updated.dueDate());
    }

    @Test
    void update_StatusToCompleted_ShouldSetCompletedAt() {
        Project created = projectRepository.create(createProjectDto);
        CreateProjectDto updateDto = new CreateProjectDto(
                testUser.id(),
                testArea.id(),
                "Completed project",
                "Done",
                null,
                "completed",
                null
        );

        Project updated = projectRepository.update(created.id(), updateDto);

        assertNotNull(updated);
        assertEquals("completed", updated.status());
        assertNotNull(updated.completedAt());
    }

    @Test
    void update_NonExistingProject_ShouldReturnNull() {
        CreateProjectDto updateDto = new CreateProjectDto(
                testUser.id(),
                null,
                "Project",
                null,
                null,
                "active",
                null
        );

        Project updated = projectRepository.update(999L, updateDto);

        assertNull(updated);
    }

    @Test
    void delete_ExistingProject_ShouldReturnTrue() {
        Project created = projectRepository.create(createProjectDto);

        boolean deleted = projectRepository.delete(created.id());

        assertTrue(deleted);
        assertTrue(projectRepository.findById(created.id()).isEmpty());
    }

    @Test
    void delete_NonExistingProject_ShouldReturnFalse() {
        boolean deleted = projectRepository.delete(999L);

        assertFalse(deleted);
    }
}
