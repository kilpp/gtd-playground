package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.ReferenceDto;
import org.gk.gtdservice.model.Reference;

public class ReferenceMapper {
    public static ReferenceDto toDto(Reference reference) {
        return new ReferenceDto(
                reference.id(),
                reference.userId(),
                reference.title(),
                reference.body(),
                reference.url(),
                reference.fileHint(),
                reference.createdAt()
        );
    }
}
