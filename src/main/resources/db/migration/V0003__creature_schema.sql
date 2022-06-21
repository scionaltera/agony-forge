CREATE TABLE connection (
  id BINARY(16) NOT NULL,
  session_username VARCHAR(36),
  session_id VARCHAR(36) UNIQUE,
  http_session_id VARCHAR(36),
  remote_address VARCHAR(16),
  oauth_username VARCHAR(191),
  name VARCHAR(191),
  disconnected TIMESTAMP NULL,
  primary_state VARCHAR(191),
  secondary_state VARCHAR(191),
  PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

CREATE TABLE creature_definition (
  id BINARY(16) NOT NULL,
  player TINYINT NOT NULL DEFAULT 0,
  name VARCHAR(191) NOT NULL,
  gender VARCHAR(10) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

CREATE TABLE creature (
  id BINARY(16) NOT NULL,
  name VARCHAR(191) NOT NULL,
  gender VARCHAR(10) NOT NULL,
  definition_id BINARY(16) NOT NULL,
  connection_id BINARY(16) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT creature_connection_fk FOREIGN KEY (connection_id) REFERENCES connection (id),
  CONSTRAINT creature_definition_fk FOREIGN KEY (definition_id) REFERENCES creature_definition (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;
