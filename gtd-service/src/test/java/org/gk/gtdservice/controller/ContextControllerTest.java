package org.gk.gtdservice.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ContextControllerTest {

    @Mock
    private ContextRepository contextRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ContextController contextController;

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
    void list_AllContexts_ShouldReturnAllContexts() {
        when(contextRepository.findAll()).thenReturn(List.of(testContext));

        List<ContextDto> result = contextController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContext.name(), result.get(0).name());
    }

    @Test
    void list_ContextsByUserId_ShouldReturnContextsForUser() {
        when(contextRepository.findByUserId(1L)).thenReturn(List.of(testContext));

        List<ContextDto> result = contextController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContext.name(), result.get(0).name());
    }

    @Test
    void get_ExistingContext_ShouldReturnContext() {
        when(contextRepository.findById(1L)).thenReturn(Optional.of(testContext));

        ContextDto result = contextController.get(1L);

        assertNotNull(result);
        assertEquals(testContext.name(), result.name());
    }

    @Test
    void get_NonExistingContext_ShouldThrowException() {
        when(contextRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contextController.get(1L));
    }

    @Test
    void create_NewContext_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextRepository.create(any())).thenReturn(testContext);

        ResponseEntity<ContextDto> response = contextController.create(createContextDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testContext.name(), response.getBody().name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contextController.create(createContextDto));
    }

    @Test
    void update_ExistingContext_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextRepository.update(1L, createContextDto)).thenReturn(testContext);

        ContextDto result = contextController.update(1L, createContextDto);

        assertNotNull(result);
        assertEquals(testContext.name(), result.name());
    }

    @Test
    void update_NonExistingContext_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(contextRepository.update(1L, createContextDto)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> contextController.update(1L, createContextDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> contextController.update(1L, createContextDto));
    }

    @Test
    void delete_ExistingContext_ShouldReturnNoContent() {
        when(contextRepository.delete(1L)).thenReturn(true);

        ResponseEntity<Void> response = contextController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingContext_ShouldThrowException() {
        when(contextRepository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> contextController.delete(1L));
    }
}
