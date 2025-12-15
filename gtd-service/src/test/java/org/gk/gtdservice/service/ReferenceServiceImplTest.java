package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateReferenceDto;
import org.gk.gtdservice.dto.ReferenceDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Reference;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.ReferenceRepository;
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
class ReferenceServiceImplTest {

    @Mock
    private ReferenceRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReferenceServiceImpl service;

    private Reference testReference;
    private CreateReferenceDto createReferenceDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testReference = new Reference(1L, 1L, "Docs", "Documentation", "http://example.com", null, Instant.now());
        createReferenceDto = new CreateReferenceDto(1L, "Docs", "Documentation", "http://example.com", null);
    }

    @Test
    void findAll_ShouldReturnAllReferences() {
        when(repository.findAll()).thenReturn(List.of(testReference));

        List<ReferenceDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReference.title(), result.get(0).title());
    }

    @Test
    void findByUserId_ShouldReturnReferencesForUser() {
        when(repository.findByUserId(1L)).thenReturn(List.of(testReference));

        List<ReferenceDto> result = service.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReference.title(), result.get(0).title());
    }

    @Test
    void findById_ExistingReference_ShouldReturnReference() {
        when(repository.findById(1L)).thenReturn(Optional.of(testReference));

        ReferenceDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(testReference.title(), result.title());
    }

    @Test
    void findById_NonExistingReference_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_ValidReference_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.create(any())).thenReturn(testReference);

        ReferenceDto result = service.create(createReferenceDto);

        assertNotNull(result);
        assertEquals(testReference.title(), result.title());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createReferenceDto));
    }

    @Test
    void update_ValidReference_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(Optional.of(testReference));

        ReferenceDto result = service.update(1L, createReferenceDto);

        assertNotNull(result);
        assertEquals(testReference.title(), result.title());
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createReferenceDto));
    }

    @Test
    void update_NonExistingReference_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createReferenceDto));
    }

    @Test
    void delete_ExistingReference_ShouldDelete() {
        when(repository.delete(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void delete_NonExistingReference_ShouldThrowException() {
        when(repository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}
