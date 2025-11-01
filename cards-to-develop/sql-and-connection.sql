--umpierre04
--host: mysql13-farm2.uni5.net
-- username: umpierre04
-- password:
-- database: umpierre04




-- GTD MySQL Schema (MySQL 8.0+)
SET NAMES utf8mb4;
SET time_zone = '+00:00';

CREATE TABLE users (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  email VARCHAR(190) UNIQUE NOT NULL,
  name VARCHAR(190) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE contexts (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(120) NOT NULL,
  description TEXT NULL,
  is_location TINYINT(1) NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_context_user_name (user_id, name),
  CONSTRAINT fk_context_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE areas (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(160) NOT NULL,
  description TEXT NULL,
  UNIQUE KEY uq_area_user_name (user_id, name),
  CONSTRAINT fk_area_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE projects (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  area_id BIGINT UNSIGNED NULL,
  title VARCHAR(255) NOT NULL,
  outcome TEXT NULL,
  notes TEXT NULL,
  status ENUM('active','on_hold','someday','completed','dropped') NOT NULL DEFAULT 'active',
  due_date DATE NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at TIMESTAMP NULL,
  INDEX idx_projects_user_status (user_id, status),
  INDEX idx_projects_area (area_id),
  CONSTRAINT fk_projects_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_projects_area FOREIGN KEY (area_id) REFERENCES areas(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE tasks (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  project_id BIGINT UNSIGNED NULL,
  context_id BIGINT UNSIGNED NULL,
  title VARCHAR(255) NOT NULL,
  notes TEXT NULL,
  status ENUM('inbox','next','waiting','scheduled','someday','reference','done','dropped') NOT NULL DEFAULT 'inbox',
  priority TINYINT UNSIGNED NULL,
  energy TINYINT UNSIGNED NULL,
  duration_est_min SMALLINT UNSIGNED NULL,
  due_at DATETIME NULL,
  defer_until DATETIME NULL,
  waiting_on VARCHAR(190) NULL,
  waiting_since DATETIME NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  completed_at DATETIME NULL,
  order_index INT NULL,
  INDEX idx_tasks_user_status (user_id, status),
  INDEX idx_tasks_context (context_id),
  INDEX idx_tasks_project (project_id),
  INDEX idx_tasks_due (due_at),
  INDEX idx_tasks_defer (defer_until),
  CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_tasks_project FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE SET NULL,
  CONSTRAINT fk_tasks_context FOREIGN KEY (context_id) REFERENCES contexts(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE task_dependencies (
  task_id BIGINT UNSIGNED NOT NULL,
  depends_on_task_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (task_id, depends_on_task_id),
  CONSTRAINT fk_dep_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_dep_depends_on FOREIGN KEY (depends_on_task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT chk_no_self_dep CHECK (task_id <> depends_on_task_id)
) ENGINE=InnoDB;

CREATE TABLE tags (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  name VARCHAR(120) NOT NULL,
  UNIQUE KEY uq_tag_user_name (user_id, name),
  CONSTRAINT fk_tags_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE task_tags (
  task_id BIGINT UNSIGNED NOT NULL,
  tag_id BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (task_id, tag_id),
  CONSTRAINT fk_task_tags_task FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE,
  CONSTRAINT fk_task_tags_tag FOREIGN KEY (tag_id) REFERENCES tags(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE references_store (
  id BIGINT UNSIGNED PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT UNSIGNED NOT NULL,
  title VARCHAR(255) NOT NULL,
  body MEDIUMTEXT NULL,
  url VARCHAR(1024) NULL,
  file_hint VARCHAR(255) NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_ref_user (user_id),
  CONSTRAINT fk_refs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE OR REPLACE VIEW v_inbox AS
SELECT t.* FROM tasks t WHERE t.status = 'inbox' ORDER BY t.created_at;

CREATE OR REPLACE VIEW v_next_actions AS
SELECT t.* FROM tasks t
WHERE t.status = 'next'
  AND (t.defer_until IS NULL OR t.defer_until <= NOW())
  AND t.completed_at IS NULL
  AND NOT EXISTS (
      SELECT 1 FROM task_dependencies d
      JOIN tasks td ON td.id = d.depends_on_task_id
      WHERE d.task_id = t.id
        AND (td.status <> 'done' OR td.completed_at IS NULL)
  )
ORDER BY COALESCE(t.priority, 99), COALESCE(t.due_at, '9999-12-31'), t.created_at;

CREATE OR REPLACE VIEW v_waiting_for AS
SELECT t.* FROM tasks t WHERE t.status = 'waiting' ORDER BY COALESCE(t.waiting_since, t.created_at);

CREATE OR REPLACE VIEW v_someday_tasks AS
SELECT t.* FROM tasks t WHERE t.status = 'someday' ORDER BY t.created_at;

CREATE OR REPLACE VIEW v_someday_projects AS
SELECT p.* FROM projects p WHERE p.status = 'someday' ORDER BY p.created_at;

CREATE OR REPLACE VIEW v_scheduled AS
SELECT t.* FROM tasks t WHERE t.status = 'scheduled' ORDER BY COALESCE(t.due_at, t.created_at);

CREATE OR REPLACE VIEW v_tickler AS
SELECT t.* FROM tasks t
WHERE t.defer_until IS NOT NULL AND t.defer_until > NOW()
  AND t.status IN ('next','waiting','someday','scheduled')
ORDER BY t.defer_until;

CREATE OR REPLACE VIEW v_active_projects_needing_next_action AS
SELECT p.* FROM projects p
WHERE p.status = 'active'
  AND NOT EXISTS (
    SELECT 1 FROM tasks t
    WHERE t.project_id = p.id
      AND t.status = 'next'
      AND (t.defer_until IS NULL OR t.defer_until <= NOW())
      AND t.completed_at IS NULL
      AND NOT EXISTS (
          SELECT 1 FROM task_dependencies d
          JOIN tasks td ON td.id = d.depends_on_task_id
          WHERE d.task_id = t.id
            AND (td.status <> 'done' OR td.completed_at IS NULL)
      )
  )
ORDER BY p.created_at;

