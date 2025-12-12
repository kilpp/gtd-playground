package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.dto.TagDto;

import java.util.List;

public interface TagService {
    List<TagDto> findAll();
    List<TagDto> findByUserId(Long userId);
    TagDto findById(Long id);
    TagDto create(CreateTagDto dto);
    TagDto update(Long id, CreateTagDto dto);
    void delete(Long id);
}
