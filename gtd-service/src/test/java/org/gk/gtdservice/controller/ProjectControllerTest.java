package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.ProjectDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Area;
import org.gk.gtdservice.model.Project;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.AreaRepository;
import org.gk.gtdservice.repo.ProjectRepository;
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
class ProjectControllerTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private ProjectController projectController;

    private Project testProject;
    private CreateProjectDto createProjectDto;
    private User testUser;
    private Area testArea;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testArea = new Area(2L, 1L, "Work", "Work area", Instant.now());
        testProject = new Project(
                1L,
                1L,
                2L,
                "Redesign website",
                "Modern responsive site",
                "Use React",
                "active",
                LocalDate.of(2025, 12, 31),
                Instant.now(),
                null
        );
        createProjectDto = new CreateProjectDto(
                1L,
                2L,
                "Redesign website",
                "Modern responsive site",
                "Use React",
                "active",
                LocalDate.of(2025, 12, 31)
        );
    }

    @Test
    void list_AllProjects_ShouldReturnAllProjects() {
        when(projectRepository.findAll()).thenReturn(List.of(testProject));

        List<ProjectDto> result = projectController.list(null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void list_ProjectsByUserId_ShouldReturnProjectsForUser() {
        when(projectRepository.findByUserId(1L)).thenReturn(List.of(testProject));

        List<ProjectDto> result = projectController.list(1L, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void list_ProjectsByAreaId_ShouldReturnProjectsForArea() {
        when(projectRepository.findByAreaId(2L)).thenReturn(List.of(testProject));

        List<ProjectDto> result = projectController.list(null, 2L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void list_ProjectsByStatus_ShouldReturnProjectsWithStatus() {
        when(projectRepository.findByStatus("active")).thenReturn(List.of(testProject));

        List<ProjectDto> result = projectController.list(null, null, "active");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void get_ExistingProject_ShouldReturnProject() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(testProject));

        ProjectDto result = projectController.get(1L);

        assertNotNull(result);
        assertEquals(testProject.title(), result.title());
    }

    @Test
    void get_NonExistingProject_ShouldThrowException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectController.get(1L));
    }

    @Test
    void create_NewProject_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(2L)).thenReturn(Optional.of(testArea));
        when(projectRepository.create(any())).thenReturn(testProject);

        ResponseEntity<ProjectDto> response = projectController.create(createProjectDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testProject.title(), response.getBody().title());
    }

    @Test
    void create_WithoutArea_ShouldReturnCreated() {
        CreateProjectDto dtoWithoutArea = new CreateProjectDto(
                1L,
                null,
                "Project",
                null,
                null,
                "active",
                null
        );
        Project projectWithoutArea = new Project(
                1L,
                1L,
                null,
                "Project",
                null,
                null,
                "active",
                null,
                Instant.now(),
                null
        );
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(projectRepository.create(any())).thenReturn(projectWithoutArea);

        ResponseEntity<ProjectDto> response = projectController.create(dtoWithoutArea);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectController.create(createProjectDto));
    }

    @Test
    void create_NonExistingArea_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectController.create(createProjectDto));
    }

    @Test
    void update_ExistingProject_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(2L)).thenReturn(Optional.of(testArea));
        when(projectRepository.update(1L, createProjectDto)).thenReturn(testProject);

        ProjectDto result = projectController.update(1L, createProjectDto);

        assertNotNull(result);
        assertEquals(testProject.title(), result.title());
    }

    @Test
    void update_NonExistingProject_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(2L)).thenReturn(Optional.of(testArea));
        when(projectRepository.update(1L, createProjectDto)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> projectController.update(1L, createProjectDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectController.update(1L, createProjectDto));
    }

    @Test
    void update_NonExistingArea_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectController.update(1L, createProjectDto));
    }

    @Test
    void delete_ExistingProject_ShouldReturnNoContent() {
        when(projectRepository.delete(1L)).thenReturn(true);

        ResponseEntity<Void> response = projectController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingProject_ShouldThrowException() {
        when(projectRepository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> projectController.delete(1L));
    }
}
