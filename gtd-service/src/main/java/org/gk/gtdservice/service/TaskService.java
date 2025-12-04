package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.TaskDto;

import java.util.List;

public interface TaskService {
    
    List<TaskDto> findAll();
    
    List<TaskDto> findByUserId(Long userId);
    
    List<TaskDto> findByProjectId(Long projectId);
    
    List<TaskDto> findByContextId(Long contextId);
    
    List<TaskDto> findByStatus(String status);
    
    TaskDto findById(Long id);
    
    TaskDto create(CreateTaskDto dto);
    
    TaskDto update(Long id, CreateTaskDto dto);
    
    void delete(Long id);
}
