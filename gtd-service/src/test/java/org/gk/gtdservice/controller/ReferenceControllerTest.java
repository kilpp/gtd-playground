package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateReferenceDto;
import org.gk.gtdservice.dto.ReferenceDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.mapper.ReferenceMapper;
import org.gk.gtdservice.model.Reference;
import org.gk.gtdservice.service.ReferenceService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferenceControllerTest {

    @Mock
    private ReferenceService referenceService;

    @InjectMocks
    private ReferenceController referenceController;

    private ReferenceDto testReferenceDto;
    private CreateReferenceDto createReferenceDto;

    @BeforeEach
    void setUp() {
        Reference testReference = new Reference(
                1L,
                1L,
                "GTD Weekly Review Checklist",
                "Checklist content...",
                "http://example.com/checklist",
                "/docs/checklist.pdf",
                Instant.now()
        );
        testReferenceDto = ReferenceMapper.toDto(testReference);
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
        when(referenceService.findAll()).thenReturn(List.of(testReferenceDto));

        List<ReferenceDto> result = referenceController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReferenceDto.title(), result.get(0).title());
    }

    @Test
    void list_ReferencesByUserId_ShouldReturnReferencesForUser() {
        when(referenceService.findByUserId(1L)).thenReturn(List.of(testReferenceDto));

        List<ReferenceDto> result = referenceController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReferenceDto.title(), result.get(0).title());
    }

    @Test
    void get_ExistingId_ShouldReturnReference() {
        when(referenceService.findById(1L)).thenReturn(testReferenceDto);

        ReferenceDto result = referenceController.get(1L);

        assertNotNull(result);
        assertEquals(testReferenceDto.id(), result.id());
    }

    @Test
    void get_NonExistingId_ShouldThrowException() {
        when(referenceService.findById(99L)).thenThrow(new ResourceNotFoundException("Reference not found"));

        assertThrows(ResourceNotFoundException.class, () -> referenceController.get(99L));
    }

    @Test
    void create_ValidDto_ShouldReturnCreatedReference() {
        when(referenceService.create(any(CreateReferenceDto.class))).thenReturn(testReferenceDto);

        ResponseEntity<ReferenceDto> response = referenceController.create(createReferenceDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testReferenceDto.title(), response.getBody().title());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(referenceService.create(any(CreateReferenceDto.class))).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> referenceController.create(createReferenceDto));
    }

    @Test
    void update_ValidDto_ShouldReturnUpdatedReference() {
        when(referenceService.update(eq(1L), any(CreateReferenceDto.class))).thenReturn(testReferenceDto);

        ReferenceDto result = referenceController.update(1L, createReferenceDto);

        assertNotNull(result);
        assertEquals(testReferenceDto.title(), result.title());
    }

    @Test
    void update_NonExistingReference_ShouldThrowException() {
        when(referenceService.update(eq(99L), any(CreateReferenceDto.class))).thenThrow(new ResourceNotFoundException("Reference not found"));

        assertThrows(ResourceNotFoundException.class, () -> referenceController.update(99L, createReferenceDto));
    }

    @Test
    void delete_ExistingId_ShouldReturnNoContent() {
        ResponseEntity<Void> response = referenceController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingId_ShouldReturnNotFound() {
        doThrow(new ResourceNotFoundException("Reference not found")).when(referenceService).delete(99L);

        assertThrows(ResourceNotFoundException.class, () -> referenceController.delete(99L));
    }
}
