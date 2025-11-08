// ...existing code...
package org.gk.gtdservice.model;

import java.time.Instant;

public record Context(Long id, Long userId, String name, String description, boolean isLocation, Instant createdAt) {
}

