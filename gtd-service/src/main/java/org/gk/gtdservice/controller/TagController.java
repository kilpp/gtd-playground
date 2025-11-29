package org.gk.gtdservice.controller;

import jakarta.validation.Valid;
import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.TagMapper;
import org.gk.gtdservice.model.Tag;
import org.gk.gtdservice.repo.TagRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private static final Logger logger = LoggerFactory.getLogger(TagController.class);

    private final TagRepository repository;
    private final UserRepository userRepository;

    public TagController(TagRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<TagDto> list(@RequestParam(required = false) Long userId) {
        logger.info("Listing tags for userId: {}", userId);
        List<Tag> tags;
        if (userId == null) {
            tags = repository.findAll();
        } else {
            tags = repository.findByUserId(userId);
        }
        logger.debug("Retrieved {} tags", tags.size());
        return tags.stream().map(TagMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TagDto get(@PathVariable Long id) {
        logger.info("Getting tag with id: {}", id);
        Tag t = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
        return TagMapper.toDto(t);
    }

    @PostMapping
    public ResponseEntity<TagDto> create(@Valid @RequestBody CreateTagDto dto) {
        logger.info("Creating tag: {}", dto);
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        var saved = repository.create(dto);
        TagDto out = TagMapper.toDto(saved);
        logger.info("Created tag with id: {}", saved.id());
        return ResponseEntity.created(URI.create("/api/tags/" + saved.id())).body(out);
    }

    @PutMapping("/{id}")
    public TagDto update(@PathVariable Long id, @Valid @RequestBody CreateTagDto dto) {
        logger.info("Updating tag with id: {}, dto: {}", id, dto);
        // ensure user exists
        if (userRepository.findById(dto.userId()).isEmpty()) {
            logger.warn("User not found for userId: {}", dto.userId());
            throw new ResourceNotFoundException("User not found");
        }
        Tag saved = repository.update(id, dto);
        if (saved == null) {
            logger.warn("Tag not found for id: {}", id);
            throw new ResourceNotFoundException("Tag not found");
        }
        logger.info("Updated tag with id: {}", id);
        return TagMapper.toDto(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting tag with id: {}", id);
        boolean deleted = repository.delete(id);
        if (!deleted) {
            logger.warn("Tag not found for deletion, id: {}", id);
            throw new ResourceNotFoundException("Tag not found");
        }
        logger.info("Deleted tag with id: {}", id);
        return ResponseEntity.noContent().build();
    }
}
