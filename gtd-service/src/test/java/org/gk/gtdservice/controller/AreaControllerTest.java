package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.service.AreaService;
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
class AreaControllerTest {

    @Mock
    private AreaService service;

    @InjectMocks
    private AreaController areaController;

    private AreaDto testAreaDto;
    private CreateAreaDto createAreaDto;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        testAreaDto = new AreaDto(1L, 1L, "Work", "Work area", Instant.now());
        createAreaDto = new CreateAreaDto(1L, "Work", "Work area");
    }

    @Test
    void list_AllAreas_ShouldReturnAllAreas() {
        when(service.findAll()).thenReturn(List.of(testAreaDto));

        List<AreaDto> result = areaController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAreaDto.name(), result.get(0).name());
    }

    @Test
    void list_AreasByUserId_ShouldReturnAreasForUser() {
        when(service.findByUserId(1L)).thenReturn(List.of(testAreaDto));

        List<AreaDto> result = areaController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testAreaDto.name(), result.get(0).name());
    }

    @Test
    void get_ExistingArea_ShouldReturnArea() {
        when(service.findById(1L)).thenReturn(testAreaDto);

        AreaDto result = areaController.get(1L);

        assertNotNull(result);
        assertEquals(testAreaDto.name(), result.name());
    }

    @Test
    void get_NonExistingArea_ShouldThrowException() {
        when(service.findById(1L)).thenThrow(new ResourceNotFoundException("Area not found"));

        assertThrows(ResourceNotFoundException.class, () -> areaController.get(1L));
    }

    @Test
    void create_NewArea_ShouldReturnCreated() {
        when(service.create(any())).thenReturn(testAreaDto);

        ResponseEntity<AreaDto> response = areaController.create(createAreaDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testAreaDto.name(), response.getBody().name());
    }

    @Test
    void update_ExistingArea_ShouldReturnUpdated() {
        when(service.update(eq(1L), any())).thenReturn(testAreaDto);

        AreaDto result = areaController.update(1L, createAreaDto);

        assertNotNull(result);
        assertEquals(testAreaDto.name(), result.name());
    }

    @Test
    void delete_ExistingArea_ShouldReturnNoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> response = areaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).delete(1L);
    }
}
