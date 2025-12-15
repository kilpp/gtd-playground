package org.gk.gtdservice.service;

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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AreaServiceImplTest {

    @Mock
    private AreaRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AreaServiceImpl service;

    private Area testArea;
    private CreateAreaDto createAreaDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testArea = new Area(1L, 1L, "Work", "Work area", Instant.now());
        createAreaDto = new CreateAreaDto(1L, "Work", "Work area");
    }

    @Test
    void findAll_ShouldReturnAllAreas() {
        when(repository.findAll()).thenReturn(List.of(testArea));

        List<AreaDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArea.name(), result.get(0).name());
    }

    @Test
    void findByUserId_ShouldReturnAreasForUser() {
        when(repository.findByUserId(1L)).thenReturn(List.of(testArea));

        List<AreaDto> result = service.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testArea.name(), result.get(0).name());
    }

    @Test
    void findById_ExistingArea_ShouldReturnArea() {
        when(repository.findById(1L)).thenReturn(Optional.of(testArea));

        AreaDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(testArea.name(), result.name());
    }

    @Test
    void findById_NonExistingArea_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_ValidArea_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.create(any())).thenReturn(testArea);

        AreaDto result = service.create(createAreaDto);

        assertNotNull(result);
        assertEquals(testArea.name(), result.name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createAreaDto));
    }

    @Test
    void update_ValidArea_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(testArea);

        AreaDto result = service.update(1L, createAreaDto);

        assertNotNull(result);
        assertEquals(testArea.name(), result.name());
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createAreaDto));
    }

    @Test
    void update_NonExistingArea_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createAreaDto));
    }

    @Test
    void delete_ExistingArea_ShouldDelete() {
        when(repository.delete(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void delete_NonExistingArea_ShouldThrowException() {
        when(repository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}
