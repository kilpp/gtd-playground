package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService service;

    @InjectMocks
    private UserController userController;

    private UserDto testUserDto;
    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        testUserDto = new UserDto(1L, "testuser", "test@example.com", "Test User", Instant.now());
        createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
    }

    @Test
    void list_ShouldReturnAllUsers() {
        when(service.findAll()).thenReturn(List.of(testUserDto));

        List<UserDto> result = userController.list();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDto.username(), result.get(0).username());
    }

    @Test
    void get_ExistingUser_ShouldReturnUser() {
        when(service.findById(1L)).thenReturn(testUserDto);

        UserDto result = userController.get(1L);

        assertNotNull(result);
        assertEquals(testUserDto.username(), result.username());
    }

    @Test
    void get_NonExistingUser_ShouldThrowException() {
        when(service.findById(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> userController.get(1L));
    }

    @Test
    void create_NewUser_ShouldReturnCreated() {
        when(service.create(any())).thenReturn(testUserDto);

        ResponseEntity<UserDto> response = userController.create(createUserDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testUserDto.username(), response.getBody().username());
    }

    @Test
    void create_ExistingUsername_ShouldThrowException() {
        when(service.create(createUserDto)).thenThrow(new DataIntegrityViolationException("Username already exists"));

        assertThrows(DataIntegrityViolationException.class, () -> userController.create(createUserDto));
    }

    @Test
    void create_ExistingEmail_ShouldThrowException() {
        when(service.create(createUserDto)).thenThrow(new DataIntegrityViolationException("Email already exists"));

        assertThrows(DataIntegrityViolationException.class, () -> userController.create(createUserDto));
    }
}
