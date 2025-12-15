package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.ProjectDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.service.ProjectService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectControllerTest {

    @Mock
    private ProjectService service;

    @InjectMocks
    private ProjectController projectController;

    private ProjectDto testProjectDto;
    private CreateProjectDto createProjectDto;

    @BeforeEach
    void setUp() {
        testProjectDto = new ProjectDto(
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
        when(service.findAll()).thenReturn(List.of(testProjectDto));

        List<ProjectDto> result = projectController.list(null, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProjectDto.title(), result.get(0).title());
    }

    @Test
    void list_ProjectsByUserId_ShouldReturnProjectsForUser() {
        when(service.findByUserId(1L)).thenReturn(List.of(testProjectDto));

        List<ProjectDto> result = projectController.list(1L, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProjectDto.title(), result.get(0).title());
    }

    @Test
    void list_ProjectsByAreaId_ShouldReturnProjectsForArea() {
        when(service.findByAreaId(2L)).thenReturn(List.of(testProjectDto));

        List<ProjectDto> result = projectController.list(null, 2L, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProjectDto.title(), result.get(0).title());
    }

    @Test
    void list_ProjectsByStatus_ShouldReturnProjectsWithStatus() {
        when(service.findByStatus("active")).thenReturn(List.of(testProjectDto));

        List<ProjectDto> result = projectController.list(null, null, "active");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProjectDto.title(), result.get(0).title());
    }

    @Test
    void get_ExistingProject_ShouldReturnProject() {
        when(service.findById(1L)).thenReturn(testProjectDto);

        ProjectDto result = projectController.get(1L);

        assertNotNull(result);
        assertEquals(testProjectDto.title(), result.title());
    }

    @Test
    void get_NonExistingProject_ShouldThrowException() {
        when(service.findById(1L)).thenThrow(new ResourceNotFoundException("Project not found"));

        assertThrows(ResourceNotFoundException.class, () -> projectController.get(1L));
    }

    @Test
    void create_NewProject_ShouldReturnCreated() {
        when(service.create(any())).thenReturn(testProjectDto);

        ResponseEntity<ProjectDto> response = projectController.create(createProjectDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testProjectDto.title(), response.getBody().title());
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
        ProjectDto projectWithoutArea = new ProjectDto(
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
        when(service.create(any())).thenReturn(projectWithoutArea);

        ResponseEntity<ProjectDto> response = projectController.create(dtoWithoutArea);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(service.create(any())).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> projectController.create(createProjectDto));
    }

    @Test
    void create_NonExistingArea_ShouldThrowException() {
        when(service.create(any())).thenThrow(new ResourceNotFoundException("Area not found"));

        assertThrows(ResourceNotFoundException.class, () -> projectController.create(createProjectDto));
    }

    @Test
    void update_ExistingProject_ShouldReturnUpdated() {
        when(service.update(eq(1L), any())).thenReturn(testProjectDto);

        ProjectDto result = projectController.update(1L, createProjectDto);

        assertNotNull(result);
        assertEquals(testProjectDto.title(), result.title());
    }

    @Test
    void update_NonExistingProject_ShouldThrowException() {
        when(service.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("Project not found"));

        assertThrows(ResourceNotFoundException.class, () -> projectController.update(1L, createProjectDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(service.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> projectController.update(1L, createProjectDto));
    }

    @Test
    void update_NonExistingArea_ShouldThrowException() {
        when(service.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("Area not found"));

        assertThrows(ResourceNotFoundException.class, () -> projectController.update(1L, createProjectDto));
    }

    @Test
    void delete_ExistingProject_ShouldReturnNoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> response = projectController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingProject_ShouldThrowException() {
        doThrow(new ResourceNotFoundException("Project not found")).when(service).delete(1L);

        assertThrows(ResourceNotFoundException.class, () -> projectController.delete(1L));
    }
}
