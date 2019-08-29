package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class HelpCommand {
    private VerbRepository verbRepository;

    @Inject
    public HelpCommand(VerbRepository verbRepository) {
        this.verbRepository = verbRepository;
    }

    @Transactional
    @CommandDescription("Shows the list of commands available to you")
    public void invoke(Creature actor, Output output) {
        List<Verb> allVerbs = verbRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        output.append("[default]All Commands:");
        allVerbs.forEach(verb -> output.append(String.format("[default]%s", verb.getName())));
    }
}
