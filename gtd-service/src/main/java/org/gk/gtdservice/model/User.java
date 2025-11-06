package org.gk.gtdservice.model;

import java.time.Instant;

public record User(Long id, String username, String email, String name, Instant createdAt) {
}