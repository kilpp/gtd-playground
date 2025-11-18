package org.gk.gtdservice.model;

import java.time.Instant;
import java.time.LocalDate;

public record Project(
        Long id,
        Long userId,
        Long areaId,
        String title,
        String outcome,
        String notes,
        String status,
        LocalDate dueDate,
        Instant createdAt,
        Instant completedAt
) {
}
