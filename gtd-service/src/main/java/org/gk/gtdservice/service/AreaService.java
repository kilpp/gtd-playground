package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;

import java.util.List;

public interface AreaService {
    List<AreaDto> findAll();
    List<AreaDto> findByUserId(Long userId);
    AreaDto findById(Long id);
    AreaDto create(CreateAreaDto dto);
    AreaDto update(Long id, CreateAreaDto dto);
    void delete(Long id);
}
