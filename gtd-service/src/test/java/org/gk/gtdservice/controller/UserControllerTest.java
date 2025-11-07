package org.gk.gtdservice.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
    }

    @Test
    void list_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(testUser));

        List<UserDto> result = userController.list();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUser.username(), result.get(0).username());
    }

    @Test
    void get_ExistingUser_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDto result = userController.get(1L);

        assertNotNull(result);
        assertEquals(testUser.username(), result.username());
    }

    @Test
    void get_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userController.get(1L));
    }

    @Test
    void create_NewUser_ShouldReturnCreated() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.create(any())).thenReturn(testUser);

        ResponseEntity<UserDto> response = userController.create(createUserDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUser.username(), response.getBody().username());
    }

    @Test
    void create_ExistingUsername_ShouldReturnConflict() {
        when(userRepository.findByUsername(createUserDto.username())).thenReturn(Optional.of(testUser));

        ResponseEntity<UserDto> response = userController.create(createUserDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }

    @Test
    void create_ExistingEmail_ShouldReturnConflict() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(createUserDto.email())).thenReturn(Optional.of(testUser));

        ResponseEntity<UserDto> response = userController.create(createUserDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
