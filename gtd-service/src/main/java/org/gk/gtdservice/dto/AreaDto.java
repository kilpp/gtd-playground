package org.gk.gtdservice.dto;

import java.time.Instant;

public record AreaDto(Long id, Long userId, String name, String description, Instant createdAt) {
}


