package org.gk.gtdservice.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferenceControllerTest {

    @Mock
    private ReferenceRepository referenceRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReferenceController referenceController;

    private Reference testReference;
    private CreateReferenceDto createReferenceDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testReference = new Reference(
                1L,
                1L,
                "GTD Weekly Review Checklist",
                "Checklist content...",
                "http://example.com/checklist",
                "/docs/checklist.pdf",
                Instant.now()
        );
        createReferenceDto = new CreateReferenceDto(
                1L,
                "GTD Weekly Review Checklist",
                "Checklist content...",
                "http://example.com/checklist",
                "/docs/checklist.pdf"
        );
    }

    @Test
    void list_AllReferences_ShouldReturnAllReferences() {
        when(referenceRepository.findAll()).thenReturn(List.of(testReference));

        List<ReferenceDto> result = referenceController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReference.title(), result.get(0).title());
    }

    @Test
    void list_ReferencesByUserId_ShouldReturnReferencesForUser() {
        when(referenceRepository.findByUserId(1L)).thenReturn(List.of(testReference));

        List<ReferenceDto> result = referenceController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReference.title(), result.get(0).title());
    }

    @Test
    void get_ExistingId_ShouldReturnReference() {
        when(referenceRepository.findById(1L)).thenReturn(Optional.of(testReference));

        ReferenceDto result = referenceController.get(1L);

        assertNotNull(result);
        assertEquals(testReference.id(), result.id());
    }

    @Test
    void get_NonExistingId_ShouldThrowException() {
        when(referenceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> referenceController.get(99L));
    }

    @Test
    void create_ValidDto_ShouldReturnCreatedReference() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(referenceRepository.create(any(CreateReferenceDto.class))).thenReturn(testReference);

        ResponseEntity<ReferenceDto> response = referenceController.create(createReferenceDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testReference.title(), response.getBody().title());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> referenceController.create(createReferenceDto));
    }

    @Test
    void update_ValidDto_ShouldReturnUpdatedReference() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(referenceRepository.update(eq(1L), any(CreateReferenceDto.class))).thenReturn(Optional.of(testReference));

        ReferenceDto result = referenceController.update(1L, createReferenceDto);

        assertNotNull(result);
        assertEquals(testReference.title(), result.title());
    }

    @Test
    void update_NonExistingReference_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(referenceRepository.update(eq(99L), any(CreateReferenceDto.class))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> referenceController.update(99L, createReferenceDto));
    }

    @Test
    void delete_ExistingId_ShouldReturnNoContent() {
        when(referenceRepository.delete(1L)).thenReturn(true);

        ResponseEntity<Void> response = referenceController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingId_ShouldReturnNotFound() {
        when(referenceRepository.delete(99L)).thenReturn(false);

        ResponseEntity<Void> response = referenceController.delete(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
