package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.binding.VerbBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

@Component
public class HelpCommand {
    private ApplicationContext applicationContext;
    private VerbRepository verbRepository;

    @Inject
    public HelpCommand(ApplicationContext applicationContext, VerbRepository verbRepository) {
        this.applicationContext = applicationContext;
        this.verbRepository = verbRepository;
    }

    @Transactional
    @CommandDescription("Shows the list of commands available to you")
    public void invoke(Creature actor, Output output) {
        List<Verb> allVerbs = verbRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));

        output.append("[default]All Commands:");
        allVerbs
            .stream()
            .filter(verb -> verb.getRoles().stream().anyMatch(verbRole -> actor.getRoles().contains(verbRole)))
            .forEach(verb -> output.append(String.format("[default]%s", verb.getName())));
    }

    @Transactional
    @CommandDescription("Shows the usages for one command")
    public void invoke(Creature actor, Output output, VerbBinding verbBinding) {
        Verb verb = verbBinding.getVerb();

        Object command = applicationContext.getBean(verb.getBean());
        Verb.showVerbSyntax(verb, command, output);
    }
}
