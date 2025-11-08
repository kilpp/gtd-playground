// ...existing code...
package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.model.Context;

import java.time.Instant;

public class ContextMapper {

    public static ContextDto toDto(Context c) {
        if (c == null) return null;
        return new ContextDto(c.id(), c.userId(), c.name(), c.description(), c.isLocation(), c.createdAt());
    }

    public static Context fromCreateDto(CreateContextDto c) {
        if (c == null) return null;
        return new Context(null, c.userId(), c.name(), c.description(), c.isLocation(), Instant.now());
    }
}

