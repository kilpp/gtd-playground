package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.model.Tag;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class TagMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        Instant now = Instant.now();
        Tag tag = new Tag(1L, 1L, "Work", now);

        TagDto dto = TagMapper.toDto(tag);

        assertEquals(tag.id(), dto.id());
        assertEquals(tag.userId(), dto.userId());
        assertEquals(tag.name(), dto.name());
        assertEquals(tag.createdAt(), dto.createdAt());
    }

    @Test
    void toDto_NullInput_ShouldReturnNull() {
        TagDto dto = TagMapper.toDto(null);
        assertNull(dto);
    }

    @Test
    void fromCreateDto_ShouldMapRequiredFields() {
        CreateTagDto createDto = new CreateTagDto(1L, "Work");

        Tag tag = TagMapper.fromCreateDto(createDto);

        assertNull(tag.id()); // ID should be null for new tags
        assertEquals(createDto.userId(), tag.userId());
        assertEquals(createDto.name(), tag.name());
        assertNotNull(tag.createdAt()); // Creation timestamp should be set
    }

    @Test
    void fromCreateDto_NullInput_ShouldReturnNull() {
        Tag tag = TagMapper.fromCreateDto(null);
        assertNull(tag);
    }

    @Test
    void fromCreateDto_ShouldSetCurrentTimestamp() {
        CreateTagDto createDto = new CreateTagDto(1L, "Work");
        Instant before = Instant.now();

        Tag tag = TagMapper.fromCreateDto(createDto);

        Instant after = Instant.now();

        assertTrue(tag.createdAt().isAfter(before) || tag.createdAt().equals(before));
        assertTrue(tag.createdAt().isBefore(after) || tag.createdAt().equals(after));
    }
}
