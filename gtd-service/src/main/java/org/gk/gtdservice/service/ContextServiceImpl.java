package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.ContextMapper;
import org.gk.gtdservice.model.Context;
import org.gk.gtdservice.repo.ContextRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContextServiceImpl implements ContextService {

    private static final Logger logger = LoggerFactory.getLogger(ContextServiceImpl.class);

    private final ContextRepository repository;
    private final UserRepository userRepository;

    public ContextServiceImpl(ContextRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContextDto> findAll() {
        logger.info("Listing all contexts");
        return repository.findAll().stream()
                .map(ContextMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContextDto> findByUserId(Long userId) {
        logger.info("Listing contexts for userId: {}", userId);
        return repository.findByUserId(userId).stream()
                .map(ContextMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ContextDto findById(Long id) {
        logger.info("Getting context with id: {}", id);
        return repository.findById(id)
                .map(ContextMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Context not found"));
    }

    @Override
    public ContextDto create(CreateContextDto dto) {
        logger.info("Creating context: {}", dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Context saved = repository.create(dto);
        return ContextMapper.toDto(saved);
    }

    @Override
    public ContextDto update(Long id, CreateContextDto dto) {
        logger.info("Updating context with id: {}, dto: {}", id, dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Context saved = repository.update(id, dto);
        if (saved == null) {
            throw new ResourceNotFoundException("Context not found");
        }
        return ContextMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting context with id: {}", id);
        if (!repository.delete(id)) {
            throw new ResourceNotFoundException("Context not found");
        }
    }
}
