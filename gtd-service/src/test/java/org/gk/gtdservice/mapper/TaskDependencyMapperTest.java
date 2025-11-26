package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;
import org.gk.gtdservice.model.TaskDependency;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskDependencyMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        TaskDependency dependency = new TaskDependency(1L, 2L);

        TaskDependencyDto dto = TaskDependencyMapper.toDto(dependency);

        assertEquals(dependency.taskId(), dto.taskId());
        assertEquals(dependency.dependsOnTaskId(), dto.dependsOnTaskId());
    }

    @Test
    void toDto_NullInput_ShouldReturnNull() {
        TaskDependencyDto dto = TaskDependencyMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void fromCreateDto_ShouldMapFields() {
        CreateTaskDependencyDto createDto = new CreateTaskDependencyDto(1L, 2L);

        TaskDependency dependency = TaskDependencyMapper.fromCreateDto(createDto);

        assertEquals(createDto.taskId(), dependency.taskId());
        assertEquals(createDto.dependsOnTaskId(), dependency.dependsOnTaskId());
    }

    @Test
    void fromCreateDto_NullInput_ShouldReturnNull() {
        TaskDependency dependency = TaskDependencyMapper.fromCreateDto(null);
        assertNull(dependency);
    }
}
