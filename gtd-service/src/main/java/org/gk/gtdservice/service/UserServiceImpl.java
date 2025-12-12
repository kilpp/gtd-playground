package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.UserMapper;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        logger.info("Listing all users");
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        logger.info("Getting user with id: {}", id);
        return repository.findById(id)
                .map(UserMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public UserDto create(CreateUserDto dto) {
        logger.info("Creating user: {}", dto);
        if (repository.findByUsername(dto.username()).isPresent()) {
            throw new DataIntegrityViolationException("Username already exists: " + dto.username());
        }
        if (repository.findByEmail(dto.email()).isPresent()) {
            throw new DataIntegrityViolationException("Email already exists: " + dto.email());
        }
        User saved = repository.create(dto);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, CreateUserDto dto) {
        logger.info("Updating user with id: {}, dto: {}", id, dto);
        User saved = repository.update(id, dto);
        if (saved == null) {
            throw new ResourceNotFoundException("User not found");
        }
        return UserMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        logger.info("Deleting user with id: {}", id);
        if (!repository.delete(id)) {
            throw new ResourceNotFoundException("User not found");
        }
    }
}
