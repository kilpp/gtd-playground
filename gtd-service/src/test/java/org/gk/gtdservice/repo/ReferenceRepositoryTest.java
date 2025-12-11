package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateReferenceDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Reference;
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
@Import({ReferenceRepository.class, UserRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class ReferenceRepositoryTest {

    @Autowired
    private ReferenceRepository referenceRepository;

    @Autowired
    private UserRepository userRepository;

    private CreateReferenceDto createReferenceDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);

        createReferenceDto = new CreateReferenceDto(
                testUser.id(),
                "GTD Weekly Review Checklist",
                "Checklist content...",
                "http://example.com/checklist",
                "/docs/checklist.pdf"
        );
    }

    @Test
    void create_ShouldInsertReference() {
        Reference reference = referenceRepository.create(createReferenceDto);

        assertNotNull(reference);
        assertNotNull(reference.id());
        assertEquals(createReferenceDto.userId(), reference.userId());
        assertEquals(createReferenceDto.title(), reference.title());
        assertEquals(createReferenceDto.body(), reference.body());
        assertEquals(createReferenceDto.url(), reference.url());
        assertEquals(createReferenceDto.fileHint(), reference.fileHint());
        assertNotNull(reference.createdAt());
    }

    @Test
    void findById_ExistingReference_ShouldReturnReference() {
        Reference created = referenceRepository.create(createReferenceDto);

        Optional<Reference> found = referenceRepository.findById(created.id());

        assertTrue(found.isPresent());
        assertEquals(created.id(), found.get().id());
        assertEquals(created.title(), found.get().title());
    }

    @Test
    void findById_NonExistingReference_ShouldReturnEmpty() {
        Optional<Reference> found = referenceRepository.findById(999L);

        assertTrue(found.isEmpty());
    }

    @Test
    void findAll_ShouldReturnAllReferences() {
        referenceRepository.create(createReferenceDto);
        CreateReferenceDto anotherDto = new CreateReferenceDto(
                testUser.id(),
                "Another Reference",
                "Body",
                null,
                null
        );
        referenceRepository.create(anotherDto);

        List<Reference> references = referenceRepository.findAll();

        assertEquals(2, references.size());
    }

    @Test
    void findByUserId_ShouldReturnReferencesForUser() {
        referenceRepository.create(createReferenceDto);
        
        CreateUserDto otherUserDto = new CreateUserDto("other", "other@example.com", "Other User");
        User otherUser = userRepository.create(otherUserDto);
        CreateReferenceDto otherReferenceDto = new CreateReferenceDto(
                otherUser.id(),
                "Other Reference",
                "Body",
                null,
                null
        );
        referenceRepository.create(otherReferenceDto);

        List<Reference> references = referenceRepository.findByUserId(testUser.id());

        assertEquals(1, references.size());
        assertEquals(testUser.id(), references.get(0).userId());
    }

    @Test
    void update_ShouldUpdateReference() {
        Reference created = referenceRepository.create(createReferenceDto);
        CreateReferenceDto updateDto = new CreateReferenceDto(
                testUser.id(),
                "Updated Title",
                "Updated Body",
                "http://updated.com",
                "/docs/updated.pdf"
        );

        Optional<Reference> updated = referenceRepository.update(created.id(), updateDto);

        assertTrue(updated.isPresent());
        assertEquals("Updated Title", updated.get().title());
        assertEquals("Updated Body", updated.get().body());
        assertEquals("http://updated.com", updated.get().url());
        assertEquals("/docs/updated.pdf", updated.get().fileHint());
    }

    @Test
    void update_NonExistingReference_ShouldReturnEmpty() {
        Optional<Reference> updated = referenceRepository.update(999L, createReferenceDto);

        assertTrue(updated.isEmpty());
    }

    @Test
    void delete_ShouldDeleteReference() {
        Reference created = referenceRepository.create(createReferenceDto);

        boolean deleted = referenceRepository.delete(created.id());

        assertTrue(deleted);
        assertTrue(referenceRepository.findById(created.id()).isEmpty());
    }

    @Test
    void delete_NonExistingReference_ShouldReturnFalse() {
        boolean deleted = referenceRepository.delete(999L);

        assertFalse(deleted);
    }
}
