package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import(UserRepository.class)
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private CreateUserDto createUserDto;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
    }

    @Test
    void create_ShouldInsertUser() {
        User user = userRepository.create(createUserDto);

        assertNotNull(user);
        assertNotNull(user.id());
        assertEquals(createUserDto.username(), user.username());
        assertEquals(createUserDto.email(), user.email());
        assertEquals(createUserDto.name(), user.name());
        assertNotNull(user.createdAt());
    }

    @Test
    void findById_ExistingUser_ShouldReturnUser() {
        User created = userRepository.create(createUserDto);

        Optional<User> found = userRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals(created.username(), found.get().username());
    }

    @Test
    void findById_NonExistingUser_ShouldReturnEmpty() {
        Optional<User> found = userRepository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findByUsername_ExistingUser_ShouldReturnUser() {
        User created = userRepository.create(createUserDto);

        Optional<User> found = userRepository.findByUsername(created.username());

        assertTrue(found.isPresent());
        assertEquals(created.id(), found.get().id());
    }

    @Test
    void findByEmail_ExistingUser_ShouldReturnUser() {
        User created = userRepository.create(createUserDto);

        Optional<User> found = userRepository.findByEmail(created.email());

        assertTrue(found.isPresent());
        assertEquals(created.id(), found.get().id());
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        User user1 = userRepository.create(createUserDto);
        User user2 = userRepository.create(new CreateUserDto("testuser2", "test2@example.com", "Test User 2"));

        List<User> users = userRepository.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> u.id().equals(user1.id())));
        assertTrue(users.stream().anyMatch(u -> u.id().equals(user2.id())));
    }
}
