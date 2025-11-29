package org.gk.gtdservice.dto;

import java.time.Instant;

public record TagDto(Long id, Long userId, String name, Instant createdAt) {
}
