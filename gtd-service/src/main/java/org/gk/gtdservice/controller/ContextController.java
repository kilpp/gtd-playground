package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.service.ContextService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/contexts")
public class ContextController {

    private final ContextService service;

    public ContextController(ContextService service) {
        this.service = service;
    }

    @GetMapping
    public List<ContextDto> list(@RequestParam(required = false) Long userId) {
        if (userId == null) {
            return service.findAll();
        } else {
            return service.findByUserId(userId);
        }
    }

    @GetMapping("/{id}")
    public ContextDto get(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<ContextDto> create(@Valid @RequestBody CreateContextDto dto) {
        ContextDto created = service.create(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping("/{id}")
    public ContextDto update(@PathVariable Long id, @Valid @RequestBody CreateContextDto dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
