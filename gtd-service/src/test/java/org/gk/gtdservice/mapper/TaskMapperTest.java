package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TaskDto;
import org.gk.gtdservice.model.Task;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TaskMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        Instant now = Instant.now();
        Instant completed = Instant.now();
        Instant dueAt = Instant.now().plusSeconds(86400);
        Instant deferUntil = Instant.now().plusSeconds(3600);
        Instant waitingSince = Instant.now().minusSeconds(3600);
        
        Task task = new Task(
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
                dueAt,
                deferUntil,
                "Manager",
                waitingSince,
                now,
                completed,
                100
        );

        TaskDto dto = TaskMapper.toDto(task);

        assertEquals(task.id(), dto.id());
        assertEquals(task.userId(), dto.userId());
        assertEquals(task.projectId(), dto.projectId());
        assertEquals(task.contextId(), dto.contextId());
        assertEquals(task.title(), dto.title());
        assertEquals(task.notes(), dto.notes());
        assertEquals(task.status(), dto.status());
        assertEquals(task.priority(), dto.priority());
        assertEquals(task.energy(), dto.energy());
        assertEquals(task.durationEstMin(), dto.durationEstMin());
        assertEquals(task.dueAt(), dto.dueAt());
        assertEquals(task.deferUntil(), dto.deferUntil());
        assertEquals(task.waitingOn(), dto.waitingOn());
        assertEquals(task.waitingSince(), dto.waitingSince());
        assertEquals(task.createdAt(), dto.createdAt());
        assertEquals(task.completedAt(), dto.completedAt());
        assertEquals(task.orderIndex(), dto.orderIndex());
    }

    @Test
    void toDto_NullInput_ShouldReturnNull() {
        TaskDto dto = TaskMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void toDto_WithNullableFields_ShouldMapCorrectly() {
        Instant now = Instant.now();
        Task task = new Task(
                1L,
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
                now,
                null,
                null
        );

        TaskDto dto = TaskMapper.toDto(task);

        assertNotNull(dto);
        assertNull(dto.projectId());
        assertNull(dto.contextId());
        assertNull(dto.notes());
        assertNull(dto.priority());
        assertNull(dto.energy());
        assertNull(dto.durationEstMin());
        assertNull(dto.dueAt());
        assertNull(dto.deferUntil());
        assertNull(dto.waitingOn());
        assertNull(dto.waitingSince());
        assertNull(dto.completedAt());
        assertNull(dto.orderIndex());
    }

    @Test
    void fromCreateDto_ShouldMapRequiredFields() {
        Instant dueAt = Instant.now().plusSeconds(86400);
        CreateTaskDto createDto = new CreateTaskDto(
                1L,
                2L,
                3L,
                "Buy running shoes",
                "Check store",
                "inbox",
                1,
                3,
                30,
                dueAt,
                null,
                null,
                null,
                100
        );

        Task task = TaskMapper.fromCreateDto(createDto);

        assertNull(task.id());
        assertEquals(createDto.userId(), task.userId());
        assertEquals(createDto.projectId(), task.projectId());
        assertEquals(createDto.contextId(), task.contextId());
        assertEquals(createDto.title(), task.title());
        assertEquals(createDto.notes(), task.notes());
        assertEquals(createDto.status(), task.status());
        assertEquals(createDto.priority(), task.priority());
        assertEquals(createDto.energy(), task.energy());
        assertEquals(createDto.durationEstMin(), task.durationEstMin());
        assertEquals(createDto.dueAt(), task.dueAt());
        assertEquals(createDto.orderIndex(), task.orderIndex());
        assertNotNull(task.createdAt());
        assertNull(task.completedAt());
    }

    @Test
    void fromCreateDto_NullInput_ShouldReturnNull() {
        Task task = TaskMapper.fromCreateDto(null);
        assertNull(task);
    }

    @Test
    void fromCreateDto_ShouldSetCurrentTimestamp() {
        CreateTaskDto createDto = new CreateTaskDto(
                1L,
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
        Instant before = Instant.now();

        Task task = TaskMapper.fromCreateDto(createDto);

        Instant after = Instant.now();

        assertTrue(task.createdAt().isAfter(before) || task.createdAt().equals(before));
        assertTrue(task.createdAt().isBefore(after) || task.createdAt().equals(after));
    }
}
