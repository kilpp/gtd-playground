package org.gk.gtdservice.service;

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

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AreaRepository areaRepository;

    @InjectMocks
    private ProjectServiceImpl service;

    private Project testProject;
    private CreateProjectDto createProjectDto;
    private User testUser;
    private Area testArea;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testArea = new Area(1L, 1L, "Work", "Work area", Instant.now());
        testProject = new Project(1L, 1L, 1L, "Test Project", "Outcome", null, "active",
                LocalDate.of(2025, 12, 31), Instant.now(), null);
        createProjectDto = new CreateProjectDto(1L, 1L, "Test Project", "Outcome", null, "active",
                LocalDate.of(2025, 12, 31));
    }

    @Test
    void findAll_ShouldReturnAllProjects() {
        when(repository.findAll()).thenReturn(List.of(testProject));

        List<ProjectDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void findByUserId_ShouldReturnProjectsForUser() {
        when(repository.findByUserId(1L)).thenReturn(List.of(testProject));

        List<ProjectDto> result = service.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void findByAreaId_ShouldReturnProjectsForArea() {
        when(repository.findByAreaId(1L)).thenReturn(List.of(testProject));

        List<ProjectDto> result = service.findByAreaId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void findByStatus_ShouldReturnProjectsForStatus() {
        when(repository.findByStatus("active")).thenReturn(List.of(testProject));

        List<ProjectDto> result = service.findByStatus("active");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProject.title(), result.get(0).title());
    }

    @Test
    void findById_ExistingProject_ShouldReturnProject() {
        when(repository.findById(1L)).thenReturn(Optional.of(testProject));

        ProjectDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(testProject.title(), result.title());
    }

    @Test
    void findById_NonExistingProject_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_ValidProject_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        when(repository.create(any())).thenReturn(testProject);

        ProjectDto result = service.create(createProjectDto);

        assertNotNull(result);
        assertEquals(testProject.title(), result.title());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createProjectDto));
    }

    @Test
    void create_NonExistingArea_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createProjectDto));
    }

    @Test
    void update_ValidProject_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        when(repository.update(eq(1L), any())).thenReturn(testProject);

        ProjectDto result = service.update(1L, createProjectDto);

        assertNotNull(result);
        assertEquals(testProject.title(), result.title());
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createProjectDto));
    }

    @Test
    void update_NonExistingArea_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createProjectDto));
    }

    @Test
    void update_NonExistingProject_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));
        when(repository.update(eq(1L), any())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createProjectDto));
    }

    @Test
    void delete_ExistingProject_ShouldDelete() {
        when(repository.delete(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void delete_NonExistingProject_ShouldThrowException() {
        when(repository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}
