package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.CreatureRepository;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WhoCommand {
    private CreatureRepository creatureRepository;

    @Inject
    public WhoCommand(CreatureRepository creatureRepository) {
        this.creatureRepository = creatureRepository;
    }

    @Transactional
    @CommandDescription("Displays the list of online players")
    public void invoke(Creature actor, Output output) {
        List<Creature> players = creatureRepository.findByConnectionIsNotNull()
            .sorted((o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()))
            .collect(Collectors.toList());

        output.append("[default]Who is online:");

        players.forEach(player -> output.append(String.format("[default]&nbsp;&nbsp;%s%s",
            player.getName(),
            player.getConnection().getDisconnected() == null ? "" : " [LINK DEAD]")));

        output
            .append("")
            .append(String.format("[default]%d player%s online.", players.size(), players.size() == 1 ? "" : "s"));
    }
}
