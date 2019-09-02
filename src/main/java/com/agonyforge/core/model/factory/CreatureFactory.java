package com.agonyforge.core.model.factory;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.CreatureDefinition;
import com.agonyforge.core.model.Role;
import com.agonyforge.core.model.repository.ConnectionRepository;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.model.repository.RoleRepository;
import com.agonyforge.core.service.CommService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.agonyforge.core.controller.interpret.PrimaryConnectionState.DISCONNECTED;

@Component
public class CreatureFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(CreatureFactory.class);

    private CommService commService;
    private CreatureRepository creatureRepository;
    private ConnectionRepository connectionRepository;
    private RoleRepository roleRepository;
    private UserDetailsManager userDetailsManager;

    @Inject
    public CreatureFactory(
        CommService commService,
        CreatureRepository creatureRepository,
        ConnectionRepository connectionRepository,
        RoleRepository roleRepository,
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") UserDetailsManager userDetailsManager) {

        this.commService = commService;
        this.creatureRepository = creatureRepository;
        this.connectionRepository = connectionRepository;
        this.roleRepository = roleRepository;
        this.userDetailsManager = userDetailsManager;
    }

    public Creature build(CreatureDefinition definition, Interpreter interpreter, Connection connection) {
        UserDetails user = userDetailsManager.loadUserByUsername(definition.getName());
        Creature creature = creatureRepository
            .findByDefinition(definition)
            .findFirst()
            .orElseGet(() -> {
                Creature c = new Creature();

                c.setDefinition(definition);
                c.setName(definition.getName());
                c.setGender(definition.getGender());
                c.getRoles().clear();
                c.getRoles().addAll(user.getAuthorities()
                    .stream()
                    .map(authority -> {
                        Optional<Role> roleOptional = roleRepository.findById(authority.getAuthority());

                        if (roleOptional.isPresent()) {
                            return roleOptional.get();
                        }

                        LOGGER.warn("User authority '{}' has no matching Role!", authority.getAuthority());
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));

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
