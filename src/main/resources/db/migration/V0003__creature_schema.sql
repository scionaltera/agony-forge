CREATE TABLE connection (
  id BINARY(16) NOT NULL,
  session_username VARCHAR(36),
  session_id VARCHAR(36),
  http_session_id VARCHAR(36),
  remote_address VARCHAR(16),
  name VARCHAR(191),
  primary_state VARCHAR(191),
  secondary_state VARCHAR(191),
  PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

CREATE TABLE creature (
  id BINARY(16) NOT NULL,
  name VARCHAR(191) NOT NULL,
  connection_id BINARY(16) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT creature_connection_fk FOREIGN KEY (connection_id) REFERENCES connection (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;
