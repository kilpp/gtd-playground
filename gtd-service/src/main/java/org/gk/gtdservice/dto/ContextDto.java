package org.gk.gtdservice.dto;

import java.time.Instant;

public record ContextDto(Long id, Long userId, String name, String description, Boolean isLocation, Instant createdAt) {
}

