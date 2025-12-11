package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateReferenceDto;
import org.gk.gtdservice.dto.ReferenceDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.ReferenceMapper;
import org.gk.gtdservice.model.Reference;
import org.gk.gtdservice.repo.ReferenceRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/references")
public class ReferenceController {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceController.class);

    private final ReferenceRepository repository;
    private final UserRepository userRepository;

    public ReferenceController(ReferenceRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ReferenceDto> list(@RequestParam(required = false) Long userId) {
        logger.info("Listing references - userId: {}", userId);
        List<Reference> references;
        if (userId != null) {
            references = repository.findByUserId(userId);
        } else {
            references = repository.findAll();
        }
        return references.stream().map(ReferenceMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ReferenceDto get(@PathVariable Long id) {
        logger.info("Getting reference with id: {}", id);
        return repository.findById(id)
                .map(ReferenceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found"));
    }

    @PostMapping
    public ResponseEntity<ReferenceDto> create(@Valid @RequestBody CreateReferenceDto dto) {
        logger.info("Creating reference: {}", dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        var saved = repository.create(dto);
        return ResponseEntity.created(URI.create("/api/references/" + saved.id()))
                .body(ReferenceMapper.toDto(saved));
    }

    @PutMapping("/{id}")
    public ReferenceDto update(@PathVariable Long id, @Valid @RequestBody CreateReferenceDto dto) {
        logger.info("Updating reference with id: {}, dto: {}", id, dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return repository.update(id, dto)
                .map(ReferenceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting reference with id: {}", id);
        if (repository.delete(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
