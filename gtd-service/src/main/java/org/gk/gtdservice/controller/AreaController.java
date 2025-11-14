package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.AreaMapper;
import org.gk.gtdservice.model.Area;
import org.gk.gtdservice.repo.AreaRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/areas")
public class AreaController {

    private final AreaRepository repository;
    private final UserRepository userRepository;

    public AreaController(AreaRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<AreaDto> list(@RequestParam(required = false) Long userId) {
        List<Area> areas;
        if (userId == null) {
            areas = repository.findAll();
        } else {
            areas = repository.findByUserId(userId);
        }
        return areas.stream().map(AreaMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public AreaDto get(@PathVariable Long id) {
        return repository.findById(id)
                .map(AreaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found"));
    }

    @PostMapping
    public ResponseEntity<AreaDto> create(@Valid @RequestBody CreateAreaDto dto) {
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        var saved = repository.create(dto);
        AreaDto out = AreaMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/areas/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public AreaDto update(@PathVariable Long id, @Valid @RequestBody CreateAreaDto dto) {
        IO.println("UPDATE AREA" + " ID=" + id + "DTO=" + dto);
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Area saved = repository.update(id, dto);
        if (saved == null) throw new ResourceNotFoundException("Area not found");
        return AreaMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = repository.delete(id);
        if (!deleted) throw new ResourceNotFoundException("Area not found");
        return ResponseEntity.noContent().build();
    }
}

