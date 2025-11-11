package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateContextDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Context;
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
@Import({ContextRepository.class, UserRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class ContextRepositoryTest {

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private CreateContextDto createContextDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);
        createContextDto = new CreateContextDto(testUser.id(), "@Home", "Home context", true);
    }

    @Test
    void create_ShouldInsertContext() {
        Context context = contextRepository.create(createContextDto);

        assertNotNull(context);
        assertNotNull(context.id());
        assertEquals(createContextDto.userId(), context.userId());
        assertEquals(createContextDto.name(), context.name());
        assertEquals(createContextDto.description(), context.description());
        assertEquals(createContextDto.isLocation(), context.isLocation());
        assertNotNull(context.createdAt());
    }

    @Test
    void findById_ExistingContext_ShouldReturnContext() {
        Context created = contextRepository.create(createContextDto);

        Optional<Context> found = contextRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals(created.name(), found.get().name());
    }

    @Test
    void findById_NonExistingContext_ShouldReturnEmpty() {
        Optional<Context> found = contextRepository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllContexts() {
        Context context1 = contextRepository.create(createContextDto);
        Context context2 = contextRepository.create(new CreateContextDto(testUser.id(), "@Office", "Office context", true));

        List<Context> contexts = contextRepository.findAll();

        assertEquals(2, contexts.size());
        assertTrue(contexts.stream().anyMatch(c -> c.id().equals(context1.id())));
        assertTrue(contexts.stream().anyMatch(c -> c.id().equals(context2.id())));
    }

    @Test
    void findByUserId_ShouldReturnContextsForUser() {
        Context context1 = contextRepository.create(createContextDto);
        Context context2 = contextRepository.create(new CreateContextDto(testUser.id(), "@Office", "Office context", true));
        CreateUserDto createUserDto2 = new CreateUserDto("testuser2", "test2@example.com", "Test User 2");
        User testUser2 = userRepository.create(createUserDto2);
        contextRepository.create(new CreateContextDto(testUser2.id(), "@Phone", "Phone context", false));

        List<Context> contexts = contextRepository.findByUserId(testUser.id());

        assertEquals(2, contexts.size());
        assertTrue(contexts.stream().anyMatch(c -> c.id().equals(context1.id())));
        assertTrue(contexts.stream().anyMatch(c -> c.id().equals(context2.id())));
    }

    @Test
    void update_ExistingContext_ShouldUpdateAndReturnContext() {
        Context created = contextRepository.create(createContextDto);
        CreateContextDto updateDto = new CreateContextDto(testUser.id(), "@Home Updated", "Updated description", false);

        Context updated = contextRepository.update(created.id(), updateDto);

        assertNotNull(updated);
        assertEquals(created.id(), updated.id());
        assertEquals(updateDto.name(), updated.name());
        assertEquals(updateDto.description(), updated.description());
        assertEquals(updateDto.isLocation(), updated.isLocation());
    }

    @Test
    void update_NonExistingContext_ShouldReturnNull() {
        CreateContextDto updateDto = new CreateContextDto(testUser.id(), "@Home", "Home context", true);

        Context updated = contextRepository.update(999L, updateDto);

        assertNull(updated);
    }

    @Test
    void delete_ExistingContext_ShouldReturnTrue() {
        Context created = contextRepository.create(createContextDto);

        boolean deleted = contextRepository.delete(created.id());

        assertTrue(deleted);
        assertTrue(contextRepository.findById(created.id()).isEmpty());
    }

    @Test
    void delete_NonExistingContext_ShouldReturnFalse() {
        boolean deleted = contextRepository.delete(999L);

        assertFalse(deleted);
    }
}
