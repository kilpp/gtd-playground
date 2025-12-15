package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Context;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.ContextRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextServiceImplTest {

    @Mock
    private ContextRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContextServiceImpl service;

    private Context testContext;
    private CreateContextDto createContextDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testContext = new Context(1L, 1L, "@Home", "Home context", true, Instant.now());
        createContextDto = new CreateContextDto(1L, "@Home", "Home context", true);
    }

    @Test
    void findAll_ShouldReturnAllContexts() {
        when(repository.findAll()).thenReturn(List.of(testContext));

        List<ContextDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContext.name(), result.get(0).name());
    }

    @Test
    void findByUserId_ShouldReturnContextsForUser() {
        when(repository.findByUserId(1L)).thenReturn(List.of(testContext));

        List<ContextDto> result = service.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContext.name(), result.get(0).name());
    }

    @Test
    void findById_ExistingContext_ShouldReturnContext() {
        when(repository.findById(1L)).thenReturn(Optional.of(testContext));

        ContextDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(testContext.name(), result.name());
    }

    @Test
    void findById_NonExistingContext_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_ValidContext_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.create(any())).thenReturn(testContext);

        ContextDto result = service.create(createContextDto);

        assertNotNull(result);
        assertEquals(testContext.name(), result.name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createContextDto));
    }

    @Test
    void update_ValidContext_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(testContext);

        ContextDto result = service.update(1L, createContextDto);

        assertNotNull(result);
        assertEquals(testContext.name(), result.name());
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createContextDto));
    }

    @Test
    void update_NonExistingContext_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createContextDto));
    }

    @Test
    void delete_ExistingContext_ShouldDelete() {
        when(repository.delete(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void delete_NonExistingContext_ShouldThrowException() {
        when(repository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}
