package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateReferenceDto;
import org.gk.gtdservice.dto.ReferenceDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.ReferenceMapper;
import org.gk.gtdservice.model.Reference;
import org.gk.gtdservice.repo.ReferenceRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReferenceServiceImpl implements ReferenceService {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceServiceImpl.class);

    private final ReferenceRepository repository;
    private final UserRepository userRepository;

    public ReferenceServiceImpl(ReferenceRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceDto> findAll() {
        logger.info("Listing all references");
        return repository.findAll().stream()
                .map(ReferenceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReferenceDto> findByUserId(Long userId) {
        logger.info("Listing references for userId: {}", userId);
        return repository.findByUserId(userId).stream()
                .map(ReferenceMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReferenceDto findById(Long id) {
        logger.info("Getting reference with id: {}", id);
        return repository.findById(id)
                .map(ReferenceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found"));
    }

    @Override
    public ReferenceDto create(CreateReferenceDto dto) {
        logger.info("Creating reference: {}", dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Reference saved = repository.create(dto);
        return ReferenceMapper.toDto(saved);
    }

    @Override
    public ReferenceDto update(Long id, CreateReferenceDto dto) {
        logger.info("Updating reference with id: {}, dto: {}", id, dto);
        if (userRepository.findById(dto.userId()).isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        return repository.update(id, dto)
                .map(ReferenceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Reference not found"));
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting reference with id: {}", id);
        if (!repository.delete(id)) {
            throw new ResourceNotFoundException("Reference not found");
        }
    }
}
