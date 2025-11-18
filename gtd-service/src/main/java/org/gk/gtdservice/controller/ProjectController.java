package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateProjectDto;
import org.gk.gtdservice.dto.ProjectDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.ProjectMapper;
import org.gk.gtdservice.model.Project;
import org.gk.gtdservice.repo.AreaRepository;
import org.gk.gtdservice.repo.ProjectRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectRepository repository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;

    public ProjectController(ProjectRepository repository, UserRepository userRepository, AreaRepository areaRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.areaRepository = areaRepository;
    }

    @GetMapping
    public List<ProjectDto> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long areaId,
            @RequestParam(required = false) String status
    ) {
        logger.info("Listing projects with filters - userId: {}, areaId: {}, status: {}", userId, areaId, status);
        List<Project> projects;
        
        if (userId != null) {
            projects = repository.findByUserId(userId);
        } else if (areaId != null) {
            projects = repository.findByAreaId(areaId);
        } else if (status != null) {
            projects = repository.findByStatus(status);
        } else {
            projects = repository.findAll();
        }
        
        logger.debug("Retrieved {} projects", projects.size());
        return projects.stream().map(ProjectMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ProjectDto get(@PathVariable Long id) {
        logger.info("Getting project with id: {}", id);
        return repository.findById(id)
                .map(ProjectMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @PostMapping
    public ResponseEntity<ProjectDto> create(@Valid @RequestBody CreateProjectDto dto) {
        logger.info("Creating project: {}", dto);
        
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        
        // ensure area exists if provided
        if (dto.areaId() != null && areaRepository.findById(dto.areaId()).isEmpty()) {
            logger.warn("Area not found for areaId: {}", dto.areaId());
            throw new ResourceNotFoundException("Area not found");
        }
        
        var saved = repository.create(dto);
        ProjectDto out = ProjectMapper.toDto(saved);
        logger.info("Created project with id: {}", saved.id());
        return ResponseEntity.created(URI.create("/api/projects/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public ProjectDto update(@PathVariable Long id, @Valid @RequestBody CreateProjectDto dto) {
        logger.info("Updating project with id: {}, dto: {}", id, dto);
        
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        
        // ensure area exists if provided
        if (dto.areaId() != null && areaRepository.findById(dto.areaId()).isEmpty()) {
            logger.warn("Area not found for areaId: {}", dto.areaId());
            throw new ResourceNotFoundException("Area not found");
        }
        
        Project saved = repository.update(id, dto);
        if (saved == null) {
            logger.warn("Project not found for id: {}", id);
            throw new ResourceNotFoundException("Project not found");
        }
        logger.info("Updated project with id: {}", id);
        return ProjectMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting project with id: {}", id);
        boolean deleted = repository.delete(id);
        if (!deleted) {
            logger.warn("Project not found for deletion, id: {}", id);
            throw new ResourceNotFoundException("Project not found");
        }
        logger.info("Deleted project with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
