CREATE SCHEMA IF NOT EXISTS gtd;

CREATE TABLE IF NOT EXISTS gtd.users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    name       VARCHAR(100),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS gtd.contexts
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    is_location BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_context_user FOREIGN KEY (user_id) REFERENCES gtd.users (id)
);

CREATE TABLE IF NOT EXISTS gtd.areas
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT NOT NULL,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_area_user FOREIGN KEY (user_id) REFERENCES gtd.users (id)
);

CREATE TABLE IF NOT EXISTS gtd.projects
(
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id      BIGINT NOT NULL,
    area_id      BIGINT,
    title        VARCHAR(200) NOT NULL,
    outcome      VARCHAR(500),
    notes        TEXT,
    status       VARCHAR(20) NOT NULL DEFAULT 'active',
    due_date     DATE,
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    CONSTRAINT fk_project_user FOREIGN KEY (user_id) REFERENCES gtd.users (id),
    CONSTRAINT fk_project_area FOREIGN KEY (area_id) REFERENCES gtd.areas (id),
    CONSTRAINT chk_project_status CHECK (status IN ('active', 'on_hold', 'someday', 'completed', 'dropped'))
);