package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateAreaDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Area;
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
@Import({AreaRepository.class, UserRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class AreaRepositoryTest {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    private CreateAreaDto createAreaDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);
        createAreaDto = new CreateAreaDto(testUser.id(), "Health", "Health area");
    }

    @Test
    void create_ShouldInsertArea() {
        Area area = areaRepository.create(createAreaDto);

        assertNotNull(area);
        assertNotNull(area.id());
        assertEquals(createAreaDto.userId(), area.userId());
        assertEquals(createAreaDto.name(), area.name());
        assertEquals(createAreaDto.description(), area.description());
        assertNotNull(area.createdAt());
    }

    @Test
    void findById_ExistingArea_ShouldReturnArea() {
        Area created = areaRepository.create(createAreaDto);

        Optional<Area> found = areaRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals(created.name(), found.get().name());
    }

    @Test
    void findById_NonExistingArea_ShouldReturnEmpty() {
        Optional<Area> found = areaRepository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllAreas() {
        Area area1 = areaRepository.create(createAreaDto);
        Area area2 = areaRepository.create(new CreateAreaDto(testUser.id(), "Work", "Work area"));

        List<Area> areas = areaRepository.findAll();

        assertEquals(2, areas.size());
        assertTrue(areas.stream().anyMatch(a -> a.id().equals(area1.id())));
        assertTrue(areas.stream().anyMatch(a -> a.id().equals(area2.id())));
    }

    @Test
    void findByUserId_ShouldReturnAreasForUser() {
        Area area1 = areaRepository.create(createAreaDto);
        Area area2 = areaRepository.create(new CreateAreaDto(testUser.id(), "Work", "Work area"));
        CreateUserDto createUserDto2 = new CreateUserDto("testuser2", "test2@example.com", "Test User 2");
        User testUser2 = userRepository.create(createUserDto2);
        areaRepository.create(new CreateAreaDto(testUser2.id(), "Personal", "Personal area"));

        List<Area> areas = areaRepository.findByUserId(testUser.id());

        assertEquals(2, areas.size());
        assertTrue(areas.stream().anyMatch(a -> a.id().equals(area1.id())));
        assertTrue(areas.stream().anyMatch(a -> a.id().equals(area2.id())));
    }

    @Test
    void update_ExistingArea_ShouldUpdateAndReturnArea() {
        Area created = areaRepository.create(createAreaDto);
        CreateAreaDto updateDto = new CreateAreaDto(testUser.id(), "Health Updated", "Updated description");

        Area updated = areaRepository.update(created.id(), updateDto);

        assertNotNull(updated);
        assertEquals(created.id(), updated.id());
        assertEquals(updateDto.name(), updated.name());
        assertEquals(updateDto.description(), updated.description());
    }

    @Test
    void update_NonExistingArea_ShouldReturnNull() {
        CreateAreaDto updateDto = new CreateAreaDto(testUser.id(), "Health", "Health area");

        Area updated = areaRepository.update(999L, updateDto);

        assertNull(updated);
    }

    @Test
    void delete_ExistingArea_ShouldReturnTrue() {
        Area created = areaRepository.create(createAreaDto);

        boolean deleted = areaRepository.delete(created.id());

        assertTrue(deleted);
        assertTrue(areaRepository.findById(created.id()).isEmpty());
    }

    @Test
    void delete_NonExistingArea_ShouldReturnFalse() {
        boolean deleted = areaRepository.delete(999L);

        assertFalse(deleted);
    }
}

