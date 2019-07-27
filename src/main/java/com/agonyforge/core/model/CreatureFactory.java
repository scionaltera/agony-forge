package com.agonyforge.core.model;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.repository.ConnectionRepository;
import com.agonyforge.core.repository.CreatureRepository;
import com.agonyforge.core.service.CommService;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import static com.agonyforge.core.model.PrimaryConnectionState.DISCONNECTED;

@Component
public class CreatureFactory {
    private CommService commService;
    private CreatureRepository creatureRepository;
    private ConnectionRepository connectionRepository;

    @Inject
    public CreatureFactory(
        CommService commService,
        CreatureRepository creatureRepository,
        ConnectionRepository connectionRepository) {

        this.commService = commService;
        this.creatureRepository = creatureRepository;
        this.connectionRepository = connectionRepository;
    }

    public Creature build(CreatureDefinition definition, Interpreter interpreter, Connection connection) {
        Creature creature = creatureRepository
            .findByDefinition(definition)
            .findFirst()
            .orElseGet(() -> {
                Creature c = new Creature();

                c.setDefinition(definition);
                c.setName(definition.getName());
                c.setGender(definition.getGender());

                return c;
            });

        if (creature.getConnection() != null && !connection.equals(creature.getConnection())) {
            Connection oldConnection = creature.getConnection();

            oldConnection.setPrimaryState(DISCONNECTED);
            connectionRepository.save(oldConnection);

            commService.echo(creature, interpreter, new Output("[yellow]This character has been reconnected in another browser. Goodbye!"));
        }

        creature.setConnection(connection);

        return creatureRepository.save(creature);
    }
}
