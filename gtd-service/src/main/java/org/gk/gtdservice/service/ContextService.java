package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;

import java.util.List;

public interface ContextService {
    List<ContextDto> findAll();
    List<ContextDto> findByUserId(Long userId);
    ContextDto findById(Long id);
    ContextDto create(CreateContextDto dto);
    ContextDto update(Long id, CreateContextDto dto);
    void delete(Long id);
}
