package org.gk.gtdservice.model;

import java.time.Instant;

public record Reference(
        Long id,
        Long userId,
        String title,
        String body,
        String url,
        String fileHint,
        Instant createdAt
) {
}
