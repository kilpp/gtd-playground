package org.gk.gtdservice.model;

import java.time.Instant;

public record Task(
        Long id,
        Long userId,
        Long projectId,
        Long contextId,
        String title,
        String notes,
        String status,
        Integer priority,
        Integer energy,
        Integer durationEstMin,
        Instant dueAt,
        Instant deferUntil,
        String waitingOn,
        Instant waitingSince,
        Instant createdAt,
        Instant completedAt,
        Integer orderIndex
) {
}
