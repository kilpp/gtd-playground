package org.gk.gtdservice.mapper;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;
import org.gk.gtdservice.model.User;

import java.time.Instant;

public class UserMapper {

    public static UserDto toDto(User u) {
        if (u == null) return null;
        return new UserDto(u.id(), u.username(), u.email(), u.name(), u.createdAt());
    }

    public static User fromCreateDto(CreateUserDto c) {
        if (c == null) return null;
        return new User(null, c.username(), c.email(), c.name(), Instant.now());
    }
}
