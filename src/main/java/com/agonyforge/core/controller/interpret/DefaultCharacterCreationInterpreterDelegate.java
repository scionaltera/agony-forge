package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureFactory;
import com.agonyforge.core.model.DefaultCharacterCreationConnectionState;
import com.agonyforge.core.repository.CreatureRepository;

import javax.inject.Inject;

import static com.agonyforge.core.model.PrimaryConnectionState.IN_GAME;

public class DefaultCharacterCreationInterpreterDelegate implements CharacterCreationInterpreterDelegate {
    private CreatureFactory creatureFactory;
    private CreatureRepository creatureRepository;

    @Inject
    public DefaultCharacterCreationInterpreterDelegate(
        CreatureFactory creatureFactory,
        CreatureRepository creatureRepository) {

        this.creatureFactory = creatureFactory;
        this.creatureRepository = creatureRepository;
    }

    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection) {
        Output output = new Output();
        DefaultCharacterCreationConnectionState secondaryState = DefaultCharacterCreationConnectionState.valueOf(connection.getSecondaryState());
        Creature creature = creatureFactory.build(connection.getName(), connection);

        connection.setPrimaryState(IN_GAME);
        connection.setSecondaryState(null);

        output.append("[yellow]Welcome, " + connection.getName() + "!");

        creature.setConnection(connection);
        creatureRepository.save(creature);

        output.append(primary.prompt(connection));

        return output;
    }

    @Override
    public Output prompt(Interpreter primary, Connection connection) {
        Output output = new Output();
        DefaultCharacterCreationConnectionState secondaryState = DefaultCharacterCreationConnectionState.valueOf(connection.getSecondaryState());

        output
            .append("[default]Genders in this game have no bearing on anything about your character except for which pronouns the game uses to refer to you.")
            .append("If you choose 'male' you will be 'he/him'.")
            .append("If you choose 'female' you will be 'she/her'.")
            .append("If you choose 'neither' you will be 'they/them'.")
            .append("[default]Are you male, female or neither? [M/F/N]: ");

        return output;
    }
}
