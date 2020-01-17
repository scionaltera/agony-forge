CREATE TABLE portal (
    id BINARY(16) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

CREATE TABLE room_exits (
    room_id BINARY(16) NOT NULL,
    exit_type VARCHAR(191) NOT NULL,
    exits_id BINARY(16) NOT NULL,
    PRIMARY KEY (room_id, exits_id),
    CONSTRAINT room_exits_1_fk FOREIGN KEY (room_id) REFERENCES room (id),
    CONSTRAINT room_exits_2_fk FOREIGN KEY (exits_id) REFERENCES portal (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

