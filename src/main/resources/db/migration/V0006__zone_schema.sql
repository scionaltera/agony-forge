CREATE TABLE zone (
    id INTEGER NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

CREATE TABLE room (
    id BINARY(16) NOT NULL,
    sequence INTEGER NOT NULL,
    zone_id INTEGER NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY room_id_sequence (id, sequence),
    CONSTRAINT room_zone_fk FOREIGN KEY (zone_id) REFERENCES zone (id)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

ALTER TABLE creature ADD COLUMN room_id BINARY(16) AFTER connection_id;
ALTER TABLE creature ADD CONSTRAINT creature_room_fk FOREIGN KEY (room_id) REFERENCES room (id);
