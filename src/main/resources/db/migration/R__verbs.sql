-- The INSERT IGNORE syntax will not insert again if the verb is already in the table, in order to avoid overwriting
-- changes you may have made inside the game.

INSERT IGNORE INTO verb (name, priority, bean) VALUES ('who', 0, 'whoCommand');
