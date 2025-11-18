package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.ProjectDto;
import org.gk.gtdservice.model.Project;

import java.time.Instant;

public class ProjectMapper {

    public static ProjectDto toDto(Project p) {
        if (p == null) return null;
        return new ProjectDto(
                p.id(),
                p.userId(),
                p.areaId(),
                p.title(),
                p.outcome(),
                p.notes(),
                p.status(),
                p.dueDate(),
                p.createdAt(),
                p.completedAt()
        );
    }

    public static Project fromCreateDto(CreateProjectDto c) {
        if (c == null) return null;
        return new Project(
                null,
                c.userId(),
                c.areaId(),
                c.title(),
                c.outcome(),
                c.notes(),
                c.status(),
                c.dueDate(),
                Instant.now(),
                null
        );
    }
}
