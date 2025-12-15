package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.ContextDto;
import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.service.ContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContextControllerTest {

    @Mock
    private ContextService service;

    @InjectMocks
    private ContextController contextController;

    private ContextDto testContextDto;
    private CreateContextDto createContextDto;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        testContextDto = new ContextDto(1L, 1L, "@Home", "Home context", true, Instant.now());
        createContextDto = new CreateContextDto(1L, "@Home", "Home context", true);
    }

    @Test
    void list_AllContexts_ShouldReturnAllContexts() {
        when(service.findAll()).thenReturn(List.of(testContextDto));

        List<ContextDto> result = contextController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContextDto.name(), result.get(0).name());
    }

    @Test
    void list_ContextsByUserId_ShouldReturnContextsForUser() {
        when(service.findByUserId(1L)).thenReturn(List.of(testContextDto));

        List<ContextDto> result = contextController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testContextDto.name(), result.get(0).name());
    }

    @Test
    void get_ExistingContext_ShouldReturnContext() {
        when(service.findById(1L)).thenReturn(testContextDto);

        ContextDto result = contextController.get(1L);

        assertNotNull(result);
        assertEquals(testContextDto.name(), result.name());
    }

    @Test
    void get_NonExistingContext_ShouldThrowException() {
        when(service.findById(1L)).thenThrow(new ResourceNotFoundException("Context not found"));

        assertThrows(ResourceNotFoundException.class, () -> contextController.get(1L));
    }

    @Test
    void create_NewContext_ShouldReturnCreated() {
        when(service.create(any())).thenReturn(testContextDto);

        ResponseEntity<ContextDto> response = contextController.create(createContextDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testContextDto.name(), response.getBody().name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(service.create(any())).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> contextController.create(createContextDto));
    }

    @Test
    void update_ExistingContext_ShouldReturnUpdated() {
        when(service.update(eq(1L), any())).thenReturn(testContextDto);

        ContextDto result = contextController.update(1L, createContextDto);

        assertNotNull(result);
        assertEquals(testContextDto.name(), result.name());
    }

    @Test
    void update_NonExistingContext_ShouldThrowException() {
        when(service.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("Context not found"));

        assertThrows(ResourceNotFoundException.class, () -> contextController.update(1L, createContextDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(service.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> contextController.update(1L, createContextDto));
    }

    @Test
    void delete_ExistingContext_ShouldReturnNoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> response = contextController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingContext_ShouldThrowException() {
        doThrow(new ResourceNotFoundException("Context not found")).when(service).delete(1L);

        assertThrows(ResourceNotFoundException.class, () -> contextController.delete(1L));
    }
}
