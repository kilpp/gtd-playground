package org.gk.gtdservice.repo;

import org.gk.gtdservice.dto.CreateReferenceDto;
import org.gk.gtdservice.model.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class ReferenceRepository {

    private static final Logger logger = LoggerFactory.getLogger(ReferenceRepository.class);

    private final NamedParameterJdbcTemplate jdbc;

    public ReferenceRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Reference> mapper = (rs, _rowNum) -> new Reference(
            rs.getLong("id"),
            rs.getLong("user_id"),
            rs.getString("title"),
            rs.getString("body"),
            rs.getString("url"),
            rs.getString("file_hint"),
            rs.getTimestamp("created_at").toInstant()
    );

    public List<Reference> findAll() {
        logger.debug("Finding all references");
        return jdbc.query("SELECT * FROM gtd.references_store ORDER BY created_at DESC", mapper);
    }

    public List<Reference> findByUserId(Long userId) {
        logger.debug("Finding references for user: {}", userId);
        return jdbc.query("SELECT * FROM gtd.references_store WHERE user_id = :userId ORDER BY created_at DESC",
                new MapSqlParameterSource("userId", userId), mapper);
    }

    public Optional<Reference> findById(Long id) {
        logger.debug("Finding reference by id: {}", id);
        var list = jdbc.query("SELECT * FROM gtd.references_store WHERE id = :id",
                new MapSqlParameterSource("id", id), mapper);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public Reference create(CreateReferenceDto dto) {
        logger.debug("Creating reference: {}", dto);
        String sql = """
                INSERT INTO gtd.references_store (user_id, title, body, url, file_hint, created_at)
                VALUES (:userId, :title, :body, :url, :fileHint, :createdAt)
                """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        Instant now = Instant.now();
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("userId", dto.userId())
                .addValue("title", dto.title())
                .addValue("body", dto.body())
                .addValue("url", dto.url())
                .addValue("fileHint", dto.fileHint())
                .addValue("createdAt", java.sql.Timestamp.from(now));

        jdbc.update(sql, params, keyHolder, new String[]{"id"});
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new DataIntegrityViolationException("Failed to retrieve generated key for reference");
        }
        Long id = key.longValue();
        return new Reference(id, dto.userId(), dto.title(), dto.body(), dto.url(), dto.fileHint(), now);
    }

    public Optional<Reference> update(Long id, CreateReferenceDto dto) {
        logger.debug("Updating reference id: {} with {}", id, dto);
        String sql = """
                UPDATE gtd.references_store
                SET title = :title, body = :body, url = :url, file_hint = :fileHint
                WHERE id = :id
                """;
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("title", dto.title())
                .addValue("body", dto.body())
                .addValue("url", dto.url())
                .addValue("fileHint", dto.fileHint());

        int updated = jdbc.update(sql, params);
        if (updated == 0) {
            return Optional.empty();
        }
        // Fetch the updated record to return it with correct created_at
        return findById(id);
    }

    public boolean delete(Long id) {
        int updated = jdbc.update("DELETE FROM gtd.references_store WHERE id = :id", new MapSqlParameterSource("id", id));
        return updated > 0;
    }
}
