package org.gk.gtdservice.dto;

import java.time.Instant;

public record ReferenceDto(
        Long id,
        Long userId,
        String title,
        String body,
        String url,
        String fileHint,
        Instant createdAt
) {
}
