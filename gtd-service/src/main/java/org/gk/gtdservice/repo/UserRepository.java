package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);

    private final NamedParameterJdbcTemplate jdbc;

    public UserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<User> mapper = (rs, _rowNum) ->
            new User(
                    rs.getLong("id"),
                    rs.getString("username"),
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getTimestamp("created_at").toInstant()
            );

    public List<User> findAll() {
        logger.info("Finding all users");
        List<User> users = jdbc.query("SELECT id, username, email, name, created_at FROM gtd.users", Collections.emptyMap(), mapper);
        logger.debug("Found {} users", users.size());
        return users;
    }

    public Optional<User> findById(Long id) {
        logger.info("Finding user by id: {}", id);
        Map<String, Object> params = Map.of("id", id);
        List<User> l = jdbc.query("SELECT id, username, email, name, created_at FROM gtd.users WHERE id = :id", params, mapper);
        Optional<User> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found user with id: {}", id);
        } else {
            logger.debug("User not found with id: {}", id);
        }
        return result;
    }

    public Optional<User> findByUsername(String username) {
        logger.info("Finding user by username: {}", username);
        Map<String, Object> params = Map.of("username", username);
        List<User> l = jdbc.query("SELECT id, username, email, name, created_at FROM gtd.users WHERE username = :username", params, mapper);
        Optional<User> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found user with username: {}", username);
        } else {
            logger.debug("User not found with username: {}", username);
        }
        return result;
    }

    public Optional<User> findByEmail(String email) {
        logger.info("Finding user by email: {}", email);
        Map<String, Object> params = Map.of("email", email);
        List<User> l = jdbc.query("SELECT id, username, email, name, created_at FROM gtd.users WHERE email = :email", params, mapper);
        Optional<User> result = l.stream().findFirst();
        if (result.isPresent()) {
            logger.debug("Found user with email: {}", email);
        } else {
            logger.debug("User not found with email: {}", email);
        }
        return result;
    }

    public User create(CreateUserDto dto) throws DataIntegrityViolationException {
        logger.info("Creating user: {}", dto);
        String sql = "INSERT INTO gtd.users (username, email, name, created_at) VALUES (:username, :email, :name, :created_at)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", dto.username())
                .addValue("email", dto.email())
                .addValue("name", dto.name())
                .addValue("created_at", Timestamp.from(Instant.now()));
        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        logger.info("Created user with id: {}", id);
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created user"));
    }

    public User update(Long id, CreateUserDto dto) {
        logger.info("Updating user with id: {}, dto: {}", id, dto);
        String sql = "UPDATE gtd.users SET username = :username, email = :email, name = :name WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", dto.username())
                .addValue("email", dto.email())
                .addValue("name", dto.name())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            logger.warn("No user updated for id: {}", id);
            return null;
        }
        logger.info("Updated user with id: {}", id);
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        logger.info("Deleting user with id: {}", id);
        int updated = jdbc.update("DELETE FROM gtd.users WHERE id = :id", Map.of("id", id));
        boolean deleted = updated > 0;
        if (deleted) {
            logger.info("Deleted user with id: {}", id);
        } else {
            logger.warn("No user deleted for id: {}", id);
        }
        return deleted;
    }
}
