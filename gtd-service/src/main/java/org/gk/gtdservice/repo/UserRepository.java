package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateUserDto;
import org.gk.gtdservice.model.User;
import org.springframework.dao.DataAccessException;
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

    private final NamedParameterJdbcTemplate jdbc;

    public UserRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<User> mapper = (rs, _rowNum) -> {
        return new User(
                rs.getLong("id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("name"),
                rs.getTimestamp("created_at").toInstant()
        );
    };

    public List<User> findAll() {
        return jdbc.query("SELECT id, username, email, name, created_at FROM users", Collections.emptyMap(), mapper);
    }

    public Optional<User> findById(Long id) {
        Map<String, Object> params = Map.of("id", id);
        List<User> l = jdbc.query("SELECT id, username, email, name, created_at FROM users WHERE id = :id", params, mapper);
        return l.stream().findFirst();
    }

    public Optional<User> findByUsername(String username) {
        Map<String, Object> params = Map.of("username", username);
        List<User> l = jdbc.query("SELECT id, username, email, name, created_at FROM users WHERE username = :username", params, mapper);
        return l.stream().findFirst();
    }

    public Optional<User> findByEmail(String email) {
        Map<String, Object> params = Map.of("email", email);
        List<User> l = jdbc.query("SELECT id, username, email, name, created_at FROM users WHERE email = :email", params, mapper);
        return l.stream().findFirst();
    }

    public User create(CreateUserDto dto) throws DataIntegrityViolationException {
        String sql = "INSERT INTO users (username, email, name, created_at) VALUES (:username, :email, :name, :created_at)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", dto.username())
                .addValue("email", dto.email())
                .addValue("name", dto.name())
                .addValue("created_at", Timestamp.from(Instant.now()));
        try {
            jdbc.update(sql, params, keyHolder);
        } catch (DataIntegrityViolationException dive) {
            throw dive;
        } catch (DataAccessException dae) {
            throw dae;
        }
        Number key = keyHolder.getKey();
        Long id = key != null ? key.longValue() : null;
        return findById(id).orElseThrow(() -> new RuntimeException("Failed to load created user"));
    }

    public User update(Long id, CreateUserDto dto) {
        String sql = "UPDATE users SET username = :username, email = :email, name = :name WHERE id = :id";
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("username", dto.username())
                .addValue("email", dto.email())
                .addValue("name", dto.name())
                .addValue("id", id);
        int updated = jdbc.update(sql, params);
        if (updated == 0) return null;
        return findById(id).orElse(null);
    }

    public boolean delete(Long id) {
        int updated = jdbc.update("DELETE FROM users WHERE id = :id", Map.of("id", id));
        return updated > 0;
    }
}
