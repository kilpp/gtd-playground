package org.gk.gtdservice.model;

import java.time.Instant;

public record Tag(Long id, Long userId, String name, Instant createdAt) {
}
