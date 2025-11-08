// ...existing code...
package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.ContextMapper;
import org.gk.gtdservice.model.Context;
import org.gk.gtdservice.repo.ContextRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/contexts")
public class ContextController {

    private final ContextRepository repository;
    private final UserRepository userRepository;

    public ContextController(ContextRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<ContextDto> list(@RequestParam(required = false) Long userId) {
        List<Context> contexts;
        if (userId == null) {
            contexts = repository.findAll();
        } else {
            contexts = repository.findByUserId(userId);
        }
        return contexts.stream().map(ContextMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ContextDto get(@PathVariable Long id) {
        Context c = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Context not found"));
        return ContextMapper.toDto(c);
    }

    @PostMapping
    public ResponseEntity<ContextDto> create(@Valid @RequestBody CreateContextDto dto) {
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        var saved = repository.create(dto);
        ContextDto out = ContextMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/contexts/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public ContextDto update(@PathVariable Long id, @Valid @RequestBody CreateContextDto dto) {
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Context saved = repository.update(id, dto);
        if (saved == null) throw new ResourceNotFoundException("Context not found");
        return ContextMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = repository.delete(id);
        if (!deleted) throw new ResourceNotFoundException("Context not found");
        return ResponseEntity.noContent().build();
    }
}

