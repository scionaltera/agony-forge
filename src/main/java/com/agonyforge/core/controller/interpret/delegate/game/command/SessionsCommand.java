package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.binding.PlayerBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.CreatureRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SessionsCommand {
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionsCommand.class);

    final private SessionRegistry sessionRegistry;
    final private CreatureRepository creatureRepository;

    @Inject
    public SessionsCommand(SessionRegistry sessionRegistry, CreatureRepository creatureRepository) {
        this.sessionRegistry = sessionRegistry;
        this.creatureRepository = creatureRepository;
    }

    @Transactional
    @CommandDescription("Lists session information for one player")
    public void invoke(Creature actor, Output output, PlayerBinding playerBinding) {
        Creature target = playerBinding.getPlayer();
        displayPlayerInfo(target, output);
    }

    @Transactional
    @CommandDescription("Lists session information for all players")
    public void invoke(Creature actor, Output output) {
        List<Creature> players = creatureRepository.findByConnectionIsNotNull()
            .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
            .collect(Collectors.toList());

        output.append("[dcyan]--= Active Players =--");
        players.forEach(player -> displayPlayerInfo(player, output));
    }

    private void displayPlayerInfo(Creature player, Output output) {
        SessionInformation info = sessionRegistry.getSessionInformation(player.getConnection().getHttpSessionId());
        DefaultOidcUser user = null;

        if (info != null && info.getPrincipal() instanceof DefaultOidcUser) {
            user = (DefaultOidcUser) info.getPrincipal();
        }

        output
            .append(String.format("[yellow]%s: [dyellow]OAuth UUID: %s [cyan]Websocket ID: %s",
                player.getName(),
                player.getConnection().getHttpSessionId(),
                player.getConnection().getSessionId()))
            .append(String.format("[yellow]IP Address: [dyellow]%s", player.getConnection().getRemoteAddress()));

        if (user != null) {
            output.append(String.format("[yellow]Name: [dyellow]%s", user.getName()));
            output.append(String.format("[yellow]Given Name: [dyellow]%s", user.getGivenName()));
            output.append(String.format("[yellow]Email: [dyellow]%s", user.getEmail()));

            // TODO make this optional with an optional command argument
            output.append(String.format("[white]%s", user));
        }
    }
}
