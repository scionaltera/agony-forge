-- The INSERT IGNORE syntax will not insert again if the verb is already in the table, in order to avoid overwriting
-- changes you may have made inside the game.

-- Create the verbs first.
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('help', 0, 'helpCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('who', 0, 'whoCommand');
INSERT IGNORE INTO verb (name, priority, bean, quoting) VALUES ('gossip', 0, 'gossipCommand', TRUE);
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('super', 1000, 'superCommand');

-- Create the default roles. SUPER is a special role that will let a player run any command.
INSERT IGNORE INTO role (name) VALUES ('PLAYER');
INSERT IGNORE INTO role (name) VALUES ('SUPER');

-- Associate verbs with roles.
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('help', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('who', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('gossip', 'PLAYER');
INSERT IGNORE INTO verb_roles (verb_name, roles_name) VALUES ('super', 'PLAYER');
