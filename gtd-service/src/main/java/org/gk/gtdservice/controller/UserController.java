package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.UserMapper;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<UserDto> list() {
        logger.info("Listing all users");
        List<User> users = repository.findAll();
        logger.debug("Retrieved {} users", users.size());
        return users.stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public UserDto get(@PathVariable Long id) {
        logger.info("Getting user with id: {}", id);
        User u = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserMapper.toDto(u);
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@Valid @RequestBody CreateUserDto dto) {
        logger.info("Creating user: {}", dto);
        if (repository.findByUsername(dto.username()).isPresent()) {
            logger.warn("Username already exists: {}", dto.username());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if (repository.findByEmail(dto.email()).isPresent()) {
            logger.warn("Email already exists: {}", dto.email());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        User saved = repository.create(dto);
        UserDto out = UserMapper.toDto(saved);
        logger.info("Created user with id: {}", saved.id());
        return ResponseEntity.created(URI.create("/api/users/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public UserDto update(@PathVariable Long id, @Valid @RequestBody CreateUserDto dto) {
        logger.info("Updating user with id: {}, dto: {}", id, dto);
        User saved = repository.update(id, dto);
        if (saved == null) {
            logger.warn("User not found for id: {}", id);
            throw new ResourceNotFoundException("User not found");
        }
        logger.info("Updated user with id: {}", id);
        return UserMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting user with id: {}", id);
        boolean deleted = repository.delete(id);
        if (!deleted) {
            logger.warn("User not found for deletion, id: {}", id);
            throw new ResourceNotFoundException("User not found");
        }
        logger.info("Deleted user with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}