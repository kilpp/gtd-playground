package org.gk.gtdservice.dto;

import java.time.Instant;

public record TaskDto(
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
