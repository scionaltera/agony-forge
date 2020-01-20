-- The INSERT IGNORE syntax will not insert again if the verb is already in the table, in order to avoid overwriting
-- changes you may have made inside the game.

-- Create the verbs first.
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('down', 0, 'downCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('east', 0, 'eastCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('north', 0, 'northCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('south', 0, 'southCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('up', 0, 'upCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('west', 0, 'westCommand');
INSERT IGNORE INTO verb (name, priority, bean, quoting) VALUES ('gossip', 10, 'gossipCommand', TRUE);
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('help', 10, 'helpCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('look', 10, 'lookCommand');
INSERT IGNORE INTO verb (name, priority, bean, quoting) VALUES ('say', 10, 'sayCommand', TRUE);
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('who', 10, 'whoCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('super', 1000, 'superCommand');

-- Create the default roles. SUPER is a special role that will let a player run any command.
INSERT IGNORE INTO role (name) VALUES ('PLAYER');
INSERT IGNORE INTO role (name) VALUES ('SUPER');

-- Associate verbs with roles.
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('down', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('east', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('north', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('south', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('up', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('west', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('gossip', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('help', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('look', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('say', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('who', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('super', 'PLAYER');
