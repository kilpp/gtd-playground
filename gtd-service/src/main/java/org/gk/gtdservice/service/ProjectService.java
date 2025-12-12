package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.ProjectDto;

import java.util.List;

public interface ProjectService {
    List<ProjectDto> findAll();
    List<ProjectDto> findByUserId(Long userId);
    List<ProjectDto> findByAreaId(Long areaId);
    List<ProjectDto> findByStatus(String status);
    ProjectDto findById(Long id);
    ProjectDto create(CreateProjectDto dto);
    ProjectDto update(Long id, CreateProjectDto dto);
    void delete(Long id);
}
