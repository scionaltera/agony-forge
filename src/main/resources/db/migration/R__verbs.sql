-- The INSERT IGNORE syntax will not insert again if the verb is already in the table, in order to avoid overwriting
-- changes you may have made inside the game.

INSERT IGNORE INTO verb (name, priority, bean) VALUES ('help', 0, 'helpCommand');
INSERT IGNORE INTO verb (name, priority, bean) VALUES ('who', 0, 'whoCommand');
INSERT IGNORE INTO verb (name, priority, bean, quoting) VALUES ('gossip', 0, 'gossipCommand', TRUE);
