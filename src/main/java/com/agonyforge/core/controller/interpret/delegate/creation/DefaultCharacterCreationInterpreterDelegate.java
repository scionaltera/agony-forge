package com.agonyforge.core.controller.interpret.delegate.creation;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.factory.CreatureFactory;
import com.agonyforge.core.model.Gender;
import com.agonyforge.core.model.repository.CreatureDefinitionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static com.agonyforge.core.model.Gender.*;
import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.IN_GAME;

public class DefaultCharacterCreationInterpreterDelegate implements CharacterCreationInterpreterDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCharacterCreationInterpreterDelegate.class);

    private CreatureFactory creatureFactory;
    private CreatureRepository creatureRepository;
    private CreatureDefinitionRepository creatureDefinitionRepository;
    private CommService commService;

    @Inject
    public DefaultCharacterCreationInterpreterDelegate(
        CreatureFactory creatureFactory,
        CreatureRepository creatureRepository,
        CreatureDefinitionRepository creatureDefinitionRepository,
        CommService commService) {

        this.creatureFactory = creatureFactory;
        this.creatureRepository = creatureRepository;
        this.creatureDefinitionRepository = creatureDefinitionRepository;
        this.commService = commService;
    }

    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection) {
        Output output = new Output();
        DefaultCharacterCreationConnectionState secondaryState = DefaultCharacterCreationConnectionState.valueOf(connection.getSecondaryState());
        Gender gender;

        switch (input.getInput().toLowerCase()) {
            case "m":
                gender = MALE;
                break;
            case "f":
                gender = FEMALE;
                break;
            case "n":
                gender = NEUTRAL;
                break;
            default:
                output.append("[default]Sorry, I didn't get that.");
                output.append(primary.prompt(connection));

                return output;
        }

        CreatureDefinition definition = new CreatureDefinition();

        definition.setPlayer(true);
        definition.setName(connection.getName());
        definition.setGender(gender);

        Creature creature = creatureFactory.build(creatureDefinitionRepository.save(definition), primary, connection);

        connection.setPrimaryState(IN_GAME);
        connection.setSecondaryState(null);

        creatureRepository.save(creature);

        output.append("[yellow]Welcome, " + connection.getName() + "!");
        output.append(primary.prompt(connection));

        commService.echoToWorld(new Output("[yellow]" + creature.getName() + " has entered the game for the first time."), primary, creature);

        LOGGER.info("New player {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());

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
            .append("If you choose 'neutral' you will be 'they/them'.")
            .append("[default]Are you male, female or neutral? [M/F/N]: ");

        return output;
    }
}
