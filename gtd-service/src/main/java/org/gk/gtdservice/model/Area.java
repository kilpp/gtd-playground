package org.gk.gtdservice.model;

import java.time.Instant;

public record Area(Long id, Long userId, String name, String description, Instant createdAt) {
}

