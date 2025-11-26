package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;
import org.gk.gtdservice.model.TaskDependency;

public class TaskDependencyMapper {

    public static TaskDependencyDto toDto(TaskDependency td) {
        if (td == null) return null;
        return new TaskDependencyDto(
                td.taskId(),
                td.dependsOnTaskId()
        );
    }

    public static TaskDependency fromCreateDto(CreateTaskDependencyDto c) {
        if (c == null) return null;
        return new TaskDependency(
                c.taskId(),
                c.dependsOnTaskId()
        );
    }
}
