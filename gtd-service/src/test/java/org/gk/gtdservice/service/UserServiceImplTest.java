package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserServiceImpl service;

    private User testUser;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        when(repository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.username(), result.get(0).username());
    }

    @Test
    void findById_ExistingUser_ShouldReturnUser() {
        when(repository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(testUser.username(), result.username());
    }

    @Test
    void findById_NonExistingUser_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_ValidUser_ShouldReturnCreated() {
        when(repository.findByUsername(any())).thenReturn(Optional.empty());
        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(repository.create(any())).thenReturn(testUser);

        UserDto result = service.create(createUserDto);

        assertNotNull(result);
        assertEquals(testUser.username(), result.username());
    }

    @Test
    void create_ExistingUsername_ShouldThrowException() {
        when(repository.findByUsername(any())).thenReturn(Optional.of(testUser));

        assertThrows(DataIntegrityViolationException.class, () -> service.create(createUserDto));
    }

    @Test
    void create_ExistingEmail_ShouldThrowException() {
        when(repository.findByUsername(any())).thenReturn(Optional.empty());
        when(repository.findByEmail(any())).thenReturn(Optional.of(testUser));

        assertThrows(DataIntegrityViolationException.class, () -> service.create(createUserDto));
    }

    @Test
    void update_ValidUser_ShouldReturnUpdated() {
        when(repository.update(eq(1L), any())).thenReturn(testUser);

        UserDto result = service.update(1L, createUserDto);

        assertNotNull(result);
        assertEquals(testUser.username(), result.username());
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(repository.update(eq(1L), any())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createUserDto));
    }

    @Test
    void delete_ExistingUser_ShouldDelete() {
        when(repository.delete(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void delete_NonExistingUser_ShouldThrowException() {
        when(repository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}
