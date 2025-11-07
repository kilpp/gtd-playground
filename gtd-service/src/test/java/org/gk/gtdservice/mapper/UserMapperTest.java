package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;
import org.gk.gtdservice.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toDto_ShouldMapAllFields() {
        Instant now = Instant.now();
        User user = new User(1L, "testuser", "test@example.com", "Test User", now);

        UserDto dto = UserMapper.toDto(user);

        assertEquals(user.id(), dto.id());
        assertEquals(user.username(), dto.username());
        assertEquals(user.email(), dto.email());
        assertEquals(user.name(), dto.name());
        assertEquals(user.createdAt(), dto.createdAt());
    }

    @Test
    void fromCreateDto_ShouldMapRequiredFields() {
        CreateUserDto createDto = new CreateUserDto("testuser", "test@example.com", "Test User");

        User user = UserMapper.fromCreateDto(createDto);

        assertNull(user.id()); // ID should be null for new users
        assertEquals(createDto.username(), user.username());
        assertEquals(createDto.email(), user.email());
        assertEquals(createDto.name(), user.name());
        assertNotNull(user.createdAt()); // Creation timestamp should be set
    }

    @Test
    void fromCreateDto_ShouldSetCurrentTimestamp() {
        CreateUserDto createDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        Instant before = Instant.now();

        User user = UserMapper.fromCreateDto(createDto);

        Instant after = Instant.now();

        assertTrue(user.createdAt().isAfter(before) || user.createdAt().equals(before));
        assertTrue(user.createdAt().isBefore(after) || user.createdAt().equals(after));
    }
}
