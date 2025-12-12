package org.gk.gtdservice.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository repository;
    private final UserRepository userRepository;
    private final AreaRepository areaRepository;

    public ProjectServiceImpl(ProjectRepository repository, UserRepository userRepository, AreaRepository areaRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.areaRepository = areaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findAll() {
        logger.info("Listing all projects");
        return repository.findAll().stream()
                .map(ProjectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findByUserId(Long userId) {
        logger.info("Listing projects for userId: {}", userId);
        return repository.findByUserId(userId).stream()
                .map(ProjectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findByAreaId(Long areaId) {
        logger.info("Listing projects for areaId: {}", areaId);
        return repository.findByAreaId(areaId).stream()
                .map(ProjectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProjectDto> findByStatus(String status) {
        logger.info("Listing projects for status: {}", status);
        return repository.findByStatus(status).stream()
                .map(ProjectMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectDto findById(Long id) {
        logger.info("Getting project with id: {}", id);
        return repository.findById(id)
                .map(ProjectMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @Override
    public ProjectDto create(CreateProjectDto dto) {
        logger.info("Creating project: {}", dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        if (dto.areaId() != null && areaRepository.findById(dto.areaId()).isEmpty()) {
            throw new ResourceNotFoundException("Area not found");
        }
        Project saved = repository.create(dto);
        return ProjectMapper.toDto(saved);
    }

    @Override
    public ProjectDto update(Long id, CreateProjectDto dto) {
        logger.info("Updating project with id: {}, dto: {}", id, dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        if (dto.areaId() != null && areaRepository.findById(dto.areaId()).isEmpty()) {
            throw new ResourceNotFoundException("Area not found");
        }
        Project saved = repository.update(id, dto);
        if (saved == null) {
            throw new ResourceNotFoundException("Project not found");
        }
        return ProjectMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting project with id: {}", id);
        if (!repository.delete(id)) {
            throw new ResourceNotFoundException("Project not found");
        }
    }
}
