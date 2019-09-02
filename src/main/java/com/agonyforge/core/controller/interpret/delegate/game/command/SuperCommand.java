package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Role;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.CreatureRepository;
import com.agonyforge.core.model.repository.VerbRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class SuperCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SuperCommand.class);

    private UserDetailsManager userDetailsManager;
    private CreatureRepository creatureRepository;
    private VerbRepository verbRepository;

    @Inject
    public SuperCommand(
        @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") UserDetailsManager userDetailsManager,
        CreatureRepository creatureRepository,
        VerbRepository verbRepository) {

        this.userDetailsManager = userDetailsManager;
        this.creatureRepository = creatureRepository;
        this.verbRepository = verbRepository;
    }

    @Transactional
    @CommandDescription("Gives you the SUPER role")
    public void invoke(Creature actor, Output output) {
        Verb verb = verbRepository
            .findFirstByNameIgnoreCaseStartingWith(Sort.by(Sort.Direction.ASC, "priority", "name"), "super")
            .orElseThrow(() -> new NullPointerException("Cannot find \"super\" verb!"));

        if (!verb.getRoles().contains(new Role("PLAYER"))) {
            LOGGER.warn("{} attempted to use the SUPER command, but it has already been used", actor.getName());
            output.append("[red]This command may only be used once.");
            return;
        }

        User user = (User) userDetailsManager.loadUserByUsername(actor.getName());
        List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());

        authorities.add(new SimpleGrantedAuthority("SUPER"));

        User updatedUser = new User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            user.isAccountNonExpired(),
            user.isCredentialsNonExpired(),
            user.isAccountNonLocked(),
            authorities);

        actor.getRoles().add(new Role("SUPER"));

        userDetailsManager.updateUser(updatedUser);
        creatureRepository.save(actor);

        verb.getRoles().clear();
        verbRepository.save(verb);

        LOGGER.warn("{} successfully used the SUPER command!", actor.getName());

        output.append("[yellow]BAMF!");
    }
}
