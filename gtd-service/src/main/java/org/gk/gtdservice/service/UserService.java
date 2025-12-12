package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();
    UserDto findById(Long id);
    UserDto create(CreateUserDto dto);
    UserDto update(Long id, CreateUserDto dto);
    void delete(Long id);
}
