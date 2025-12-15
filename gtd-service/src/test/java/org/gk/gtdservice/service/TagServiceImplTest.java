package org.gk.gtdservice.service;

import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.model.Tag;
import org.gk.gtdservice.model.User;
import org.gk.gtdservice.repo.TagRepository;
import org.gk.gtdservice.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository repository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TagServiceImpl service;

    private Tag testTag;
    private CreateTagDto createTagDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testTag = new Tag(1L, 1L, "Urgent", Instant.now());
        createTagDto = new CreateTagDto(1L, "Urgent");
    }

    @Test
    void findAll_ShouldReturnAllTags() {
        when(repository.findAll()).thenReturn(List.of(testTag));

        List<TagDto> result = service.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTag.name(), result.get(0).name());
    }

    @Test
    void findByUserId_ShouldReturnTagsForUser() {
        when(repository.findByUserId(1L)).thenReturn(List.of(testTag));

        List<TagDto> result = service.findByUserId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTag.name(), result.get(0).name());
    }

    @Test
    void findById_ExistingTag_ShouldReturnTag() {
        when(repository.findById(1L)).thenReturn(Optional.of(testTag));

        TagDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(testTag.name(), result.name());
    }

    @Test
    void findById_NonExistingTag_ShouldThrowException() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    void create_ValidTag_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.create(any())).thenReturn(testTag);

        TagDto result = service.create(createTagDto);

        assertNotNull(result);
        assertEquals(testTag.name(), result.name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.create(createTagDto));
    }

    @Test
    void update_ValidTag_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(testTag);

        TagDto result = service.update(1L, createTagDto);

        assertNotNull(result);
        assertEquals(testTag.name(), result.name());
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createTagDto));
    }

    @Test
    void update_NonExistingTag_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(repository.update(eq(1L), any())).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> service.update(1L, createTagDto));
    }

    @Test
    void delete_ExistingTag_ShouldDelete() {
        when(repository.delete(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).delete(1L);
    }

    @Test
    void delete_NonExistingTag_ShouldThrowException() {
        when(repository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
    }
}
