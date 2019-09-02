CREATE TABLE role (
    name VARCHAR(191) NOT NULL,
    PRIMARY KEY (name)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

CREATE TABLE verb_roles (
    verb_name VARCHAR(191) NOT NULL,
    roles_name VARCHAR(191) NOT NULL,
    PRIMARY KEY (verb_name, roles_name),
    CONSTRAINT verb_role_1_fk FOREIGN KEY (verb_name) REFERENCES verb (name),
    CONSTRAINT verb_role_2_fk FOREIGN KEY (roles_name) REFERENCES role (name)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;

CREATE TABLE creature_roles (
    creature_id BINARY(16) NOT NULL,
    roles_name VARCHAR(191) NOT NULL,
    PRIMARY KEY (creature_id, roles_name),
    CONSTRAINT creature_role_1_fk FOREIGN KEY (creature_id) REFERENCES creature (id),
    CONSTRAINT creature_role_2_fk FOREIGN KEY (roles_name) REFERENCES role (name)
) ENGINE=InnoDB CHARACTER SET=utf8mb4, COLLATE=utf8mb4_unicode_ci;
