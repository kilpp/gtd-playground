package org.gk.gtdservice.controller;

import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.dto.TagDto;
import org.gk.gtdservice.exception.ResourceNotFoundException;
import org.gk.gtdservice.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService service;

    @InjectMocks
    private TagController tagController;

    private TagDto testTagDto;
    private CreateTagDto createTagDto;

    @BeforeEach
    void setUp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        testTagDto = new TagDto(1L, 1L, "Work", Instant.now());
        createTagDto = new CreateTagDto(1L, "Work");
    }

    @Test
    void list_AllTags_ShouldReturnAllTags() {
        when(service.findAll()).thenReturn(List.of(testTagDto));

        List<TagDto> result = tagController.list(null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTagDto.name(), result.get(0).name());
    }

    @Test
    void list_TagsByUserId_ShouldReturnTagsForUser() {
        when(service.findByUserId(1L)).thenReturn(List.of(testTagDto));

        List<TagDto> result = tagController.list(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTagDto.name(), result.get(0).name());
    }

    @Test
    void get_ExistingTag_ShouldReturnTag() {
        when(service.findById(1L)).thenReturn(testTagDto);

        TagDto result = tagController.get(1L);

        assertNotNull(result);
        assertEquals(testTagDto.name(), result.name());
    }

    @Test
    void get_NonExistingTag_ShouldThrowException() {
        when(service.findById(1L)).thenThrow(new ResourceNotFoundException("Tag not found"));

        assertThrows(ResourceNotFoundException.class, () -> tagController.get(1L));
    }

    @Test
    void create_NewTag_ShouldReturnCreated() {
        when(service.create(any())).thenReturn(testTagDto);

        ResponseEntity<TagDto> response = tagController.create(createTagDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testTagDto.name(), response.getBody().name());
    }

    @Test
    void create_NonExistingUser_ShouldThrowException() {
        when(service.create(any())).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> tagController.create(createTagDto));
    }

    @Test
    void update_ExistingTag_ShouldReturnUpdated() {
        when(service.update(eq(1L), any())).thenReturn(testTagDto);

        TagDto result = tagController.update(1L, createTagDto);

        assertNotNull(result);
        assertEquals(testTagDto.name(), result.name());
    }

    @Test
    void update_NonExistingTag_ShouldThrowException() {
        when(service.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("Tag not found"));

        assertThrows(ResourceNotFoundException.class, () -> tagController.update(1L, createTagDto));
    }

    @Test
    void update_NonExistingUser_ShouldThrowException() {
        when(service.update(eq(1L), any())).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> tagController.update(1L, createTagDto));
    }

    @Test
    void delete_ExistingTag_ShouldReturnNoContent() {
        doNothing().when(service).delete(1L);

        ResponseEntity<Void> response = tagController.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void delete_NonExistingTag_ShouldThrowException() {
        doThrow(new ResourceNotFoundException("Tag not found")).when(service).delete(1L);

        assertThrows(ResourceNotFoundException.class, () -> tagController.delete(1L));
    }
}
