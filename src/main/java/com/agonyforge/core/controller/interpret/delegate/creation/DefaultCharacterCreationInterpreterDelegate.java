package com.agonyforge.core.controller.interpret.delegate.creation;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.Zone;
import com.agonyforge.core.model.factory.CreatureFactory;
import com.agonyforge.core.model.Gender;
import com.agonyforge.core.model.factory.ZoneFactory;
import com.agonyforge.core.model.repository.CreatureDefinitionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.agonyforge.core.model.Gender.*;
import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.IN_GAME;

public class DefaultCharacterCreationInterpreterDelegate implements CharacterCreationInterpreterDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCharacterCreationInterpreterDelegate.class);

    private CreatureFactory creatureFactory;
    private CreatureRepository creatureRepository;
    private CreatureDefinitionRepository creatureDefinitionRepository;
    private ZoneFactory zoneFactory;
    private CommService commService;

    @Inject
    public DefaultCharacterCreationInterpreterDelegate(
        CreatureFactory creatureFactory,
        CreatureRepository creatureRepository,
        CreatureDefinitionRepository creatureDefinitionRepository,
        ZoneFactory zoneFactory,
        CommService commService) {

        this.creatureFactory = creatureFactory;
        this.creatureRepository = creatureRepository;
        this.creatureDefinitionRepository = creatureDefinitionRepository;
        this.zoneFactory = zoneFactory;
        this.commService = commService;
    }

    @Override
    public Output interpret(Interpreter primary, Input input, Connection connection) {
        Output output = new Output();
        String selection = input.getInput().substring(0, 1).toLowerCase();
        Optional<Gender> genderOptional = Arrays.stream(values())
            .filter(g -> !"object".equals(g.getName()))
            .filter(g -> g.getName().startsWith(selection))
            .findFirst();

        if (!genderOptional.isPresent()) {
            output.append("[default]Sorry, I didn't get that.");
            output.append(primary.prompt(connection));

            return output;
        }

        CreatureDefinition definition = new CreatureDefinition();

        definition.setPlayer(true);
        definition.setName(connection.getName());
        definition.setGender(genderOptional.get());

        Creature creature = creatureFactory.build(creatureDefinitionRepository.save(definition), primary, connection);

        connection.setPrimaryState(IN_GAME);
        connection.setSecondaryState(null);

        creatureRepository.save(creature);

        output.append("[yellow]Welcome, " + connection.getName() + "!");
        output.append(primary.prompt(connection));

        Zone zone = zoneFactory.getStartZone();

        LOGGER.info("Placed {} in Zone {}", creature.getName(), zone.getId());

        commService.echoToWorld(new Output("[yellow]" + creature.getName() + " has entered the game for the first time."), primary, creature);

        LOGGER.info("New player {} {}@{}", connection.getName(), connection.getSessionId(), connection.getRemoteAddress());

        return output;
    }

    @Override
    public Output prompt(Interpreter primary, Connection connection) {
        Output output = new Output("[default]Which pronouns should the game use to refer to your character?");

        Arrays.stream(values())
            .filter(gender -> !"object".equals(gender.getName()))
            .forEach(gender -> output.append(String.format("Choose '%s' for '%s/%s'.",
                gender.getName(),
                gender.getSubject(),
                gender.getObject())));

        output.append(String.format("[default]Please select one. [%s]: ",
            Arrays.stream(values())
                .filter(gender -> !"object".equals(gender.getName()))
                .map(gender -> gender.getName().substring(0, 1).toUpperCase())
                .collect(Collectors.joining("/"))));

        return output;
    }
}
