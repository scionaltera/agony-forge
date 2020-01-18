package com.agonyforge.core.config;

import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.controller.interpret.delegate.game.command.MoveCommand;
import com.agonyforge.core.model.Direction;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class MovementConfiguration {
    private CreatureRepository creatureRepository;
    private CommService commService;
    private Interpreter interpreter;

    @Inject
    public MovementConfiguration(
        CreatureRepository creatureRepository,
        CommService commService,
        Interpreter interpreter) {

        this.creatureRepository = creatureRepository;
        this.commService = commService;
        this.interpreter = interpreter;
    }

    @Bean(name = "northCommand")
    public MoveCommand northCommand() {
        return new MoveCommand(
            creatureRepository,
            commService,
            interpreter,
            Direction.NORTH
        );
    }

    @Bean(name = "eastCommand")
    public MoveCommand eastCommand() {
        return new MoveCommand(
            creatureRepository,
            commService,
            interpreter,
            Direction.EAST
        );
    }

    @Bean(name = "southCommand")
    public MoveCommand southCommand() {
        return new MoveCommand(
            creatureRepository,
            commService,
            interpreter,
            Direction.SOUTH
        );
    }

    @Bean(name = "westCommand")
    public MoveCommand westCommand() {
        return new MoveCommand(
            creatureRepository,
            commService,
            interpreter,
            Direction.WEST
        );
    }

    @Bean(name = "upCommand")
    public MoveCommand upCommand() {
        return new MoveCommand(
            creatureRepository,
            commService,
            interpreter,
            Direction.UP
        );
    }

    @Bean(name = "downCommand")
    public MoveCommand downCommand() {
        return new MoveCommand(
            creatureRepository,
            commService,
            interpreter,
            Direction.DOWN
        );
    }
}
