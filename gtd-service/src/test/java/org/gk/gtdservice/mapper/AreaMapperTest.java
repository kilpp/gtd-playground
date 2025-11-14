package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.model.Area;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class AreaMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        Instant now = Instant.now();
        Area area = new Area(1L, 1L, "Health", "Health area", now);

        AreaDto dto = AreaMapper.toDto(area);

        assertEquals(area.id(), dto.id());
        assertEquals(area.userId(), dto.userId());
        assertEquals(area.name(), dto.name());
        assertEquals(area.description(), dto.description());
        assertEquals(area.createdAt(), dto.createdAt());
    }

    @Test
    void toDto_NullInput_ShouldReturnNull() {
        AreaDto dto = AreaMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void fromCreateDto_ShouldMapRequiredFields() {
        CreateAreaDto createDto = new CreateAreaDto(1L, "Health", "Health area");

        Area area = AreaMapper.fromCreateDto(createDto);

        assertNull(area.id());
        assertEquals(createDto.userId(), area.userId());
        assertEquals(createDto.name(), area.name());
        assertEquals(createDto.description(), area.description());
        assertNotNull(area.createdAt());
    }

    @Test
    void fromCreateDto_NullInput_ShouldReturnNull() {
        Area area = AreaMapper.fromCreateDto(null);
        assertNull(area);
    }

    @Test
    void fromCreateDto_ShouldSetCurrentTimestamp() {
        CreateAreaDto createDto = new CreateAreaDto(1L, "Health", "Health area");
        Instant before = Instant.now();

        Area area = AreaMapper.fromCreateDto(createDto);

        Instant after = Instant.now();

        assertTrue(area.createdAt().isAfter(before) || area.createdAt().equals(before));
        assertTrue(area.createdAt().isBefore(after) || area.createdAt().equals(after));
    }
}

