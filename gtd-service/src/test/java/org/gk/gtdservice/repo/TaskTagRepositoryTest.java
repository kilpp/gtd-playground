package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateTagDto;
import org.gk.gtdservice.dto.CreateTaskDto;
import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.Tag;
import org.gk.gtdservice.model.Task;
import org.gk.gtdservice.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@Import({TaskTagRepository.class, TaskRepository.class, TagRepository.class, UserRepository.class})
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql"})
class TaskTagRepositoryTest {

    @Autowired
    private TaskTagRepository taskTagRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Task testTask;
    private Tag testTag;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("testuser", "test@example.com", "Test User");
        testUser = userRepository.create(createUserDto);

        CreateTaskDto createTaskDto = new CreateTaskDto(
                testUser.id(),
                null,
                null,
                "Test Task",
                "Notes",
                "inbox",
                1,
                3,
                30,
                Instant.now(),
                null,
                null,
                null,
                null
        );
        testTask = taskRepository.create(createTaskDto);

        CreateTagDto createTagDto = new CreateTagDto(testUser.id(), "Test Tag");
        testTag = tagRepository.create(createTagDto);
    }

    @Test
    void addTagToTask_ShouldAddTag() {
        taskTagRepository.addTagToTask(testTask.id(), testTag.id());

        List<Tag> tags = taskTagRepository.findTagsByTaskId(testTask.id());
        assertEquals(1, tags.size());
        assertEquals(testTag.id(), tags.get(0).id());
    }

    @Test
    void removeTagFromTask_ShouldRemoveTag() {
        taskTagRepository.addTagToTask(testTask.id(), testTag.id());
        
        taskTagRepository.removeTagFromTask(testTask.id(), testTag.id());

        List<Tag> tags = taskTagRepository.findTagsByTaskId(testTask.id());
        assertTrue(tags.isEmpty());
    }

    @Test
    void findTagsByTaskId_ShouldReturnTags() {
        taskTagRepository.addTagToTask(testTask.id(), testTag.id());

        List<Tag> tags = taskTagRepository.findTagsByTaskId(testTask.id());
        
        assertNotNull(tags);
        assertEquals(1, tags.size());
        assertEquals(testTag.name(), tags.get(0).name());
    }
}
