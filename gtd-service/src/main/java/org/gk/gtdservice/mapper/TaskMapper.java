package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TaskDto;
import org.gk.gtdservice.model.Task;

import java.time.Instant;

public class TaskMapper {

    public static TaskDto toDto(Task t) {
        if (t == null) return null;
        return new TaskDto(
                t.id(),
                t.userId(),
                t.projectId(),
                t.contextId(),
                t.title(),
                t.notes(),
                t.status(),
                t.priority(),
                t.energy(),
                t.durationEstMin(),
                t.dueAt(),
                t.deferUntil(),
                t.waitingOn(),
                t.waitingSince(),
                t.createdAt(),
                t.completedAt(),
                t.orderIndex()
        );
    }

    public static Task fromCreateDto(CreateTaskDto c) {
        if (c == null) return null;
        return new Task(
                null,
                c.userId(),
                c.projectId(),
                c.contextId(),
                c.title(),
                c.notes(),
                c.status(),
                c.priority(),
                c.energy(),
                c.durationEstMin(),
                c.dueAt(),
                c.deferUntil(),
                c.waitingOn(),
                c.waitingSince(),
                Instant.now(),
                null,
                c.orderIndex()
        );
    }
}
