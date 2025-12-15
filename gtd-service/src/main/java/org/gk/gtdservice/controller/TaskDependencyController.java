package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateTaskDependencyDto;
import org.gk.gtdservice.dto.TaskDependencyDto;
import org.gk.gtdservice.service.TaskDependencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/task-dependencies")
public class TaskDependencyController {

    private final TaskDependencyService service;

    public TaskDependencyController(TaskDependencyService service) {
        this.service = service;
    }

    @GetMapping
    public List<TaskDependencyDto> list(
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long dependsOnTaskId
    ) {
        if (taskId != null) {
            return service.findByTaskId(taskId);
        } else if (dependsOnTaskId != null) {
            return service.findByDependsOnTaskId(dependsOnTaskId);
        } else {
            return service.findAll();
        }
    }

    @GetMapping("/{taskId}/{dependsOnTaskId}")
    public TaskDependencyDto get(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId) {
        return service.findById(taskId, dependsOnTaskId);
    }

    @PostMapping
    public ResponseEntity<TaskDependencyDto> create(@Valid @RequestBody CreateTaskDependencyDto dto) {
        TaskDependencyDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{taskId}/{dependsOnTaskId}")
                .buildAndExpand(created.taskId(), created.dependsOnTaskId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @DeleteMapping("/{taskId}/{dependsOnTaskId}")
    public ResponseEntity<Void> delete(@PathVariable Long taskId, @PathVariable Long dependsOnTaskId) {
        service.delete(taskId, dependsOnTaskId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/task/{taskId}")
    public ResponseEntity<Void> deleteByTaskId(@PathVariable Long taskId) {
        service.deleteByTaskId(taskId);
        return ResponseEntity.noContent().build();
    }
}
