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

CREATE TABLE IF NOT EXISTS gtd.tasks
(
    id                BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id           BIGINT       NOT NULL,
    project_id        BIGINT,
    context_id        BIGINT,
    title             VARCHAR(500) NOT NULL,
    notes             TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'inbox',
    priority          INT,
    energy            INT,
    duration_est_min  INT,
    due_at            TIMESTAMP,
    defer_until       TIMESTAMP,
    waiting_on        VARCHAR(200),
    waiting_since     TIMESTAMP,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at      TIMESTAMP,
    order_index       INT,
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES gtd.users (id),
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES gtd.projects (id),
    CONSTRAINT fk_task_context FOREIGN KEY (context_id) REFERENCES gtd.contexts (id),
    CONSTRAINT chk_task_status CHECK (status IN ('inbox', 'next', 'waiting', 'scheduled', 'someday', 'reference', 'done', 'dropped')),
    CONSTRAINT chk_task_energy CHECK (energy >= 1 AND energy <= 5)
);

CREATE TABLE IF NOT EXISTS gtd.task_dependencies
(
    task_id            BIGINT NOT NULL,
    depends_on_task_id BIGINT NOT NULL,
    PRIMARY KEY (task_id, depends_on_task_id),
    CONSTRAINT fk_task_dependency_task FOREIGN KEY (task_id) REFERENCES gtd.tasks (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_dependency_depends_on FOREIGN KEY (depends_on_task_id) REFERENCES gtd.tasks (id) ON DELETE CASCADE,
    CONSTRAINT chk_no_self_dependency CHECK (task_id != depends_on_task_id)
);

CREATE TABLE IF NOT EXISTS gtd.tags
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT NOT NULL,
    name       VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tag_user FOREIGN KEY (user_id) REFERENCES gtd.users (id),
    CONSTRAINT uq_tag_user_name UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS gtd.task_tags
(
    task_id BIGINT NOT NULL,
    tag_id  BIGINT NOT NULL,
    PRIMARY KEY (task_id, tag_id),
    CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id) REFERENCES gtd.tasks (id) ON DELETE CASCADE,
    CONSTRAINT fk_task_tags_tag FOREIGN KEY (tag_id) REFERENCES gtd.tags (id) ON DELETE CASCADE
);