package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Tag;
import org.gk.gtdservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({TagRepository.class, UserRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class TagRepositoryTest {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    private CreateTagDto createTagDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);
        createTagDto = new CreateTagDto(testUser.id(), "Work");
    }

    @Test
    void create_ShouldInsertTag() {
        Tag tag = tagRepository.create(createTagDto);

        assertNotNull(tag);
        assertNotNull(tag.id());
        assertEquals(createTagDto.userId(), tag.userId());
        assertEquals(createTagDto.name(), tag.name());
        assertNotNull(tag.createdAt());
    }

    @Test
    void findById_ExistingTag_ShouldReturnTag() {
        Tag created = tagRepository.create(createTagDto);

        Optional<Tag> found = tagRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals(created.name(), found.get().name());
    }

    @Test
    void findById_NonExistingTag_ShouldReturnEmpty() {
        Optional<Tag> found = tagRepository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllTags() {
        Tag tag1 = tagRepository.create(createTagDto);
        Tag tag2 = tagRepository.create(new CreateTagDto(testUser.id(), "Health"));

        List<Tag> tags = tagRepository.findAll();

        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(t -> t.id().equals(tag1.id())));
        assertTrue(tags.stream().anyMatch(t -> t.id().equals(tag2.id())));
    }

    @Test
    void findByUserId_ShouldReturnTagsForUser() {
        Tag tag1 = tagRepository.create(createTagDto);
        Tag tag2 = tagRepository.create(new CreateTagDto(testUser.id(), "Health"));
        CreateUserDto createUserDto2 = new CreateUserDto("testuser2", "test2@example.com", "Test User 2");
        User testUser2 = userRepository.create(createUserDto2);
        tagRepository.create(new CreateTagDto(testUser2.id(), "Personal"));

        List<Tag> tags = tagRepository.findByUserId(testUser.id());

        assertEquals(2, tags.size());
        assertTrue(tags.stream().anyMatch(t -> t.id().equals(tag1.id())));
        assertTrue(tags.stream().anyMatch(t -> t.id().equals(tag2.id())));
    }

    @Test
    void update_ExistingTag_ShouldUpdateAndReturnTag() {
        Tag created = tagRepository.create(createTagDto);
        CreateTagDto updateDto = new CreateTagDto(testUser.id(), "Work Updated");

        Tag updated = tagRepository.update(created.id(), updateDto);

        assertNotNull(updated);
        assertEquals(created.id(), updated.id());
        assertEquals(updateDto.name(), updated.name());
    }

    @Test
    void update_NonExistingTag_ShouldReturnNull() {
        CreateTagDto updateDto = new CreateTagDto(testUser.id(), "Work");

        Tag updated = tagRepository.update(999L, updateDto);

        assertNull(updated);
    }

    @Test
    void delete_ExistingTag_ShouldReturnTrue() {
        Tag created = tagRepository.create(createTagDto);

        boolean deleted = tagRepository.delete(created.id());

        assertTrue(deleted);
        assertTrue(tagRepository.findById(created.id()).isEmpty());
    }

    @Test
    void delete_NonExistingTag_ShouldReturnFalse() {
        boolean deleted = tagRepository.delete(999L);

        assertFalse(deleted);
    }
}
