package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.ProjectDto;
import org.gk.gtdservice.model.Project;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ProjectMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        Instant now = Instant.now();
        Instant completed = Instant.now();
        LocalDate dueDate = LocalDate.of(2025, 12, 31);
        Project project = new Project(
                1L,
                1L,
                2L,
                "Redesign website",
                "Modern responsive site",
                "Use React",
                "active",
                dueDate,
                now,
                completed
        );

        ProjectDto dto = ProjectMapper.toDto(project);

        assertEquals(project.id(), dto.id());
        assertEquals(project.userId(), dto.userId());
        assertEquals(project.areaId(), dto.areaId());
        assertEquals(project.title(), dto.title());
        assertEquals(project.outcome(), dto.outcome());
        assertEquals(project.notes(), dto.notes());
        assertEquals(project.status(), dto.status());
        assertEquals(project.dueDate(), dto.dueDate());
        assertEquals(project.createdAt(), dto.createdAt());
        assertEquals(project.completedAt(), dto.completedAt());
    }

    @Test
    void toDto_NullInput_ShouldReturnNull() {
        ProjectDto dto = ProjectMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_WithNullableFields_ShouldMapCorrectly() {
        Instant now = Instant.now();
        Project project = new Project(
                1L,
                1L,
                null,
                "Project",
                null,
                null,
                "active",
                null,
                now,
                null
        );

        ProjectDto dto = ProjectMapper.toDto(project);

        assertNotNull(dto);
        assertNull(dto.areaId());
        assertNull(dto.outcome());
        assertNull(dto.notes());
        assertNull(dto.dueDate());
        assertNull(dto.completedAt());
    }

    @Test
    void fromCreateDto_ShouldMapRequiredFields() {
        CreateProjectDto createDto = new CreateProjectDto(
                1L,
                2L,
                "Redesign website",
                "Modern site",
                "Notes here",
                "active",
                LocalDate.of(2025, 12, 31)
        );

        Project project = ProjectMapper.fromCreateDto(createDto);

        assertNull(project.id());
        assertEquals(createDto.userId(), project.userId());
        assertEquals(createDto.areaId(), project.areaId());
        assertEquals(createDto.title(), project.title());
        assertEquals(createDto.outcome(), project.outcome());
        assertEquals(createDto.notes(), project.notes());
        assertEquals(createDto.status(), project.status());
        assertEquals(createDto.dueDate(), project.dueDate());
        assertNotNull(project.createdAt());
        assertNull(project.completedAt());
    }

    @Test
    void fromCreateDto_NullInput_ShouldReturnNull() {
        Project project = ProjectMapper.fromCreateDto(null);
        assertNull(project);
    }

    @Test
    void fromCreateDto_ShouldSetCurrentTimestamp() {
        CreateProjectDto createDto = new CreateProjectDto(
                1L,
                null,
                "Project",
                null,
                null,
                "active",
                null
        );
        Instant before = Instant.now();

        Project project = ProjectMapper.fromCreateDto(createDto);

        Instant after = Instant.now();

        assertTrue(project.createdAt().isAfter(before) || project.createdAt().equals(before));
        assertTrue(project.createdAt().isBefore(after) || project.createdAt().equals(after));
    }
}
