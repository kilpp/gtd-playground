package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.UserMapper;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UserDto> list() {
        return repository.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        User u = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toDto(u);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserDto dto) {
        if (repository.findByUsername(dto.username()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (repository.findByEmail(dto.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User u = UserMapper.fromCreateDto(dto);
        User saved = repository.create(dto);
        UserDto out = UserMapper.toDto(saved);
        return ResponseEntity.created(URI.create("/api/users/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody CreateUserDto dto) {
        User saved = repository.update(id, dto);
        if (saved == null) throw new ResourceNotFoundException("User not found");
        return UserMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean deleted = repository.delete(id);
        if (!deleted) throw new ResourceNotFoundException("User not found");
        return ResponseEntity.noContent().build();
    }
}