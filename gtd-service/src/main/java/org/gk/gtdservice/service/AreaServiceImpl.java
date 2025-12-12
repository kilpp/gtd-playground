package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.AreaMapper;
import org.gk.gtdservice.model.Area;
import org.gk.gtdservice.repo.AreaRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AreaServiceImpl implements AreaService {

    private static final Logger logger = LoggerFactory.getLogger(AreaServiceImpl.class);

    private final AreaRepository repository;
    private final UserRepository userRepository;

    public AreaServiceImpl(AreaRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaDto> findAll() {
        logger.info("Listing all areas");
        return repository.findAll().stream()
                .map(AreaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AreaDto> findByUserId(Long userId) {
        logger.info("Listing areas for userId: {}", userId);
        return repository.findByUserId(userId).stream()
                .map(AreaMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AreaDto findById(Long id) {
        logger.info("Getting area with id: {}", id);
        return repository.findById(id)
                .map(AreaMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Area not found"));
    }

    @Override
    public AreaDto create(CreateAreaDto dto) {
        logger.info("Creating area: {}", dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Area saved = repository.create(dto);
        return AreaMapper.toDto(saved);
    }

    @Override
    public AreaDto update(Long id, CreateAreaDto dto) {
        logger.info("Updating area with id: {}, dto: {}", id, dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Area saved = repository.update(id, dto);
        if (saved == null) {
            throw new ResourceNotFoundException("Area not found");
        }
        return AreaMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting area with id: {}", id);
        if (!repository.delete(id)) {
            throw new ResourceNotFoundException("Area not found");
        }
    }
}
