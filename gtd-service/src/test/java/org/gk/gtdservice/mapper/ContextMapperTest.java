package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.model.Context;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ContextMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        Instant now = Instant.now();
        Context context = new Context(1L, 1L, "@Home", "Home context", true, now);

        ContextDto dto = ContextMapper.toDto(context);

        assertEquals(context.id(), dto.id());
        assertEquals(context.userId(), dto.userId());
        assertEquals(context.name(), dto.name());
        assertEquals(context.description(), dto.description());
        assertEquals(context.isLocation(), dto.isLocation());
        assertEquals(context.createdAt(), dto.createdAt());
    }

    @Test
    void toDto_NullInput_ShouldReturnNull() {
        ContextDto dto = ContextMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void fromCreateDto_ShouldMapRequiredFields() {
        CreateContextDto createDto = new CreateContextDto(1L, "@Home", "Home context", true);

        Context context = ContextMapper.fromCreateDto(createDto);

        assertNull(context.id()); // ID should be null for new contexts
        assertEquals(createDto.userId(), context.userId());
        assertEquals(createDto.name(), context.name());
        assertEquals(createDto.description(), context.description());
        assertEquals(createDto.isLocation(), context.isLocation());
        assertNotNull(context.createdAt()); // Creation timestamp should be set
    }

    @Test
    void fromCreateDto_NullInput_ShouldReturnNull() {
        Context context = ContextMapper.fromCreateDto(null);
        assertNull(context);
    }

    @Test
    void fromCreateDto_ShouldSetCurrentTimestamp() {
        CreateContextDto createDto = new CreateContextDto(1L, "@Home", "Home context", true);
        Instant before = Instant.now();

        Context context = ContextMapper.fromCreateDto(createDto);

        Instant after = Instant.now();

        assertTrue(context.createdAt().isAfter(before) || context.createdAt().equals(before));
        assertTrue(context.createdAt().isBefore(after) || context.createdAt().equals(after));
    }
}
