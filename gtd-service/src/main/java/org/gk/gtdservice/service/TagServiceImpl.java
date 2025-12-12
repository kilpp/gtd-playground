package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.TagMapper;
import org.gk.gtdservice.model.Tag;
import org.gk.gtdservice.repo.TagRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagServiceImpl implements TagService {

    private static final Logger logger = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository repository;
    private final UserRepository userRepository;

    public TagServiceImpl(TagRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> findAll() {
        logger.info("Listing all tags");
        return repository.findAll().stream()
                .map(TagMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TagDto> findByUserId(Long userId) {
        logger.info("Listing tags for userId: {}", userId);
        return repository.findByUserId(userId).stream()
                .map(TagMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TagDto findById(Long id) {
        logger.info("Getting tag with id: {}", id);
        return repository.findById(id)
                .map(TagMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Tag not found"));
    }

    @Override
    public TagDto create(CreateTagDto dto) {
        logger.info("Creating tag: {}", dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Tag saved = repository.create(dto);
        return TagMapper.toDto(saved);
    }

    @Override
    public TagDto update(Long id, CreateTagDto dto) {
        logger.info("Updating tag with id: {}, dto: {}", id, dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Tag saved = repository.update(id, dto);
        if (saved == null) {
            throw new ResourceNotFoundException("Tag not found");
        }
        return TagMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting tag with id: {}", id);
        if (!repository.delete(id)) {
            throw new ResourceNotFoundException("Tag not found");
        }
    }
}
