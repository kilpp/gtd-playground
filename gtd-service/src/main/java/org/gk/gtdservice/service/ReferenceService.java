package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateReferenceDto;
import org.gk.gtdservice.dto.ReferenceDto;

import java.util.List;

public interface ReferenceService {
    List<ReferenceDto> findAll();
    List<ReferenceDto> findByUserId(Long userId);
    ReferenceDto findById(Long id);
    ReferenceDto create(CreateReferenceDto dto);
    ReferenceDto update(Long id, CreateReferenceDto dto);
    void delete(Long id);
}
