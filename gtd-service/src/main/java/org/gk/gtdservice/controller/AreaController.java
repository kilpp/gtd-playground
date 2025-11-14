package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.AreaMapper;
import org.gk.gtdservice.model.Area;
import org.gk.gtdservice.repo.AreaRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private static final Logger logger = LoggerFactory.getLogger(AreaController.class);

    private final AreaRepository repository;
    private final UserRepository userRepository;

    public AreaController(AreaRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<AreaDto> list(@RequestParam(required = false) Long userId) {
        logger.info("Listing areas for userId: {}", userId);
        List<Area> areas;
        if (userId == null) {
            areas = repository.findAll();
        } else {
            areas = repository.findByUserId(userId);
        }
        logger.debug("Retrieved {} areas", areas.size());
        return areas.stream().map(AreaMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AreaDto get(@PathVariable Long id) {
        logger.info("Getting area with id: {}", id);
        return repository.findById(id)
                .map(AreaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found"));
    }

    @PostMapping
    public ResponseEntity<AreaDto> create(@Valid @RequestBody CreateAreaDto dto) {
        logger.info("Creating area: {}", dto);
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        var saved = repository.create(dto);
        AreaDto out = AreaMapper.toDto(saved);
        logger.info("Created area with id: {}", saved.id());
        return ResponseEntity.created(URI.create("/api/areas/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public AreaDto update(@PathVariable Long id, @Valid @RequestBody CreateAreaDto dto) {
        logger.info("Updating area with id: {}, dto: {}", id, dto);
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        Area saved = repository.update(id, dto);
        if (saved == null) {
            logger.warn("Area not found for id: {}", id);
            throw new ResourceNotFoundException("Area not found");
        }
        logger.info("Updated area with id: {}", id);
        return AreaMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting area with id: {}", id);
        boolean deleted = repository.delete(id);
        if (!deleted) {
            logger.warn("Area not found for deletion, id: {}", id);
            throw new ResourceNotFoundException("Area not found");
        }
        logger.info("Deleted area with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
