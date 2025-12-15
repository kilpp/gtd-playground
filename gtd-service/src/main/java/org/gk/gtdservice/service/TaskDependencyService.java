package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;

import java.util.List;

public interface TaskDependencyService {
    List<TaskDependencyDto> findAll();
    List<TaskDependencyDto> findByTaskId(Long taskId);
    List<TaskDependencyDto> findByDependsOnTaskId(Long dependsOnTaskId);
    TaskDependencyDto findById(Long taskId, Long dependsOnTaskId);
    TaskDependencyDto create(CreateTaskDependencyDto dto);
    void delete(Long taskId, Long dependsOnTaskId);
    int deleteByTaskId(Long taskId);
}
