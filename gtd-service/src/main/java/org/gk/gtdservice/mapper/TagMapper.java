package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.model.Tag;

import java.time.Instant;

public class TagMapper {

    public static TagDto toDto(Tag t) {
        if (t == null) return null;
        return new TagDto(t.id(), t.userId(), t.name(), t.createdAt());
    }

    public static Tag fromCreateDto(CreateTagDto t) {
        if (t == null) return null;
        return new Tag(null, t.userId(), t.name(), Instant.now());
    }
}
