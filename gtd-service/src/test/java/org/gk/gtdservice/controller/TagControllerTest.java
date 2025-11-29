package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.dto.CreateTagDto;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TagController tagController;

    private Tag testTag;
    private CreateTagDto createTagDto;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User(1L, "testuser", "test@example.com", "Test User", Instant.now());
        testTag = new Tag(1L, 1L, "Work", Instant.now());
        createTagDto = new CreateTagDto(1L, "Work");
    }

    @Test
    void list_AllTags_ShouldReturnAllTags() {
        when(tagRepository.findAll()).thenReturn(List.of(testTag));

        List<TagDto> result = tagController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTag.name(), result.get(0).name());
    }

    @Test
    void list_TagsByUserId_ShouldReturnTagsForUser() {
        when(tagRepository.findByUserId(1L)).thenReturn(List.of(testTag));

        List<TagDto> result = tagController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTag.name(), result.get(0).name());
    }

    @Test
    void get_ExistingTag_ShouldReturnTag() {
        when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));

        TagDto result = tagController.get(1L);

        assertNotNull(result);
        assertEquals(testTag.name(), result.name());
    }

    @Test
    void get_NonExistingTag_ShouldThrowException() {
        when(tagRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagController.get(1L));
    }

    @Test
    void create_NewTag_ShouldReturnCreated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tagRepository.create(any())).thenReturn(testTag);

        ResponseEntity<TagDto> response = tagController.create(createTagDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTag.name(), response.getBody().name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagController.create(createTagDto));
    }

    @Test
    void update_ExistingTag_ShouldReturnUpdated() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tagRepository.update(1L, createTagDto)).thenReturn(testTag);

        TagDto result = tagController.update(1L, createTagDto);

        assertNotNull(result);
        assertEquals(testTag.name(), result.name());
    }

    @Test
    void update_NonExistingTag_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(tagRepository.update(1L, createTagDto)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class, () -> tagController.update(1L, createTagDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tagController.update(1L, createTagDto));
    }

    @Test
    void delete_ExistingTag_ShouldReturnNoContent() {
        when(tagRepository.delete(1L)).thenReturn(true);

        ResponseEntity<Void> response = tagController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingTag_ShouldThrowException() {
        when(tagRepository.delete(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> tagController.delete(1L));
    }
}
