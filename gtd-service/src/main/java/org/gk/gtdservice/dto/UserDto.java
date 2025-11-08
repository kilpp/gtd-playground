package org.gk.gtdservice.dto;

import java.time.Instant;

public record UserDto(Long id, String username, String email, String name, Instant createdAt) {
}
