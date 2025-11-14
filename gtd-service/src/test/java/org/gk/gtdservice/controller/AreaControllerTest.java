package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.AreaDto;
import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Area;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.AreaRepository;
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
class AreaControllerTest {

    @Mock
    private AreaRepository areaRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AreaController areaController;

    private Area testArea;
    private CreateAreaDto createAreaDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testArea = new Area(1L, 1L, "Health", "Health area", Instant.now());
        createAreaDto = new CreateAreaDto(1L, "Health", "Health area");
    }

    @Test
    void list_AllAreas_ShouldReturnAllAreas() {
        when(areaRepository.findAll()).thenReturn(List.of(testArea));

        List<AreaDto> result = areaController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArea.name(), result.get(0).name());
    }

    @Test
    void list_AreasByUserId_ShouldReturnAreasForUser() {
        when(areaRepository.findByUserId(1L)).thenReturn(List.of(testArea));

        List<AreaDto> result = areaController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArea.name(), result.get(0).name());
    }

    @Test
    void get_ExistingArea_ShouldReturnArea() {
        when(areaRepository.findById(1L)).thenReturn(Optional.of(testArea));

        AreaDto result = areaController.get(1L);

        assertNotNull(result);
        assertEquals(testArea.name(), result.name());
    }

    @Test
    void get_NonExistingArea_ShouldThrowException() {
        when(areaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> areaController.get(1L));
    }

    @Test
    void create_NewArea_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.create(any())).thenReturn(testArea);

        ResponseEntity<AreaDto> response = areaController.create(createAreaDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testArea.name(), response.getBody().name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> areaController.create(createAreaDto));
    }

    @Test
    void update_ExistingArea_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.update(1L, createAreaDto)).thenReturn(testArea);

        AreaDto result = areaController.update(1L, createAreaDto);

        assertNotNull(result);
        assertEquals(testArea.name(), result.name());
    }

    @Test
    void update_NonExistingArea_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(areaRepository.update(1L, createAreaDto)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> areaController.update(1L, createAreaDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> areaController.update(1L, createAreaDto));
    }

    @Test
    void delete_ExistingArea_ShouldReturnNoContent() {
        when(areaRepository.delete(1L)).thenReturn(true);

        ResponseEntity<Void> response = areaController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingArea_ShouldThrowException() {
        when(areaRepository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> areaController.delete(1L));
    }
}

