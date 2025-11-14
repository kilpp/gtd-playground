package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.model.Area;

import java.time.Instant;

public class AreaMapper {

    public static AreaDto toDto(Area a) {
        if (a == null) return null;
        return new AreaDto(a.id(), a.userId(), a.name(), a.description(), a.createdAt());
    }

    public static Area fromCreateDto(CreateAreaDto c) {
        if (c == null) return null;
        return new Area(null, c.userId(), c.name(), c.description(), Instant.now());
    }
}

