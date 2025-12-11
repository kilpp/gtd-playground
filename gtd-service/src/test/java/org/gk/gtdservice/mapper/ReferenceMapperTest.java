package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.ReferenceDto;
import org.gk.gtdservice.model.Reference;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReferenceMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        Instant now = Instant.now();
        Reference reference = new Reference(
                1L,
                1L,
                "GTD Weekly Review Checklist",
                "Checklist content...",
                "http://example.com/checklist",
                "/docs/checklist.pdf",
                now
        );

        ReferenceDto dto = ReferenceMapper.toDto(reference);

        assertEquals(reference.id(), dto.id());
        assertEquals(reference.userId(), dto.userId());
        assertEquals(reference.title(), dto.title());
        assertEquals(reference.body(), dto.body());
        assertEquals(reference.url(), dto.url());
        assertEquals(reference.fileHint(), dto.fileHint());
        assertEquals(reference.createdAt(), dto.createdAt());
    }
}
