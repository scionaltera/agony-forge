package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope(scopeName = "prototype")
@BindingDescription("command")
public class VerbBinding implements ArgumentBinding {
    private VerbRepository verbRepository;
    private Verb verb;

    @Inject
    public VerbBinding(VerbRepository verbRepository) {
        this.verbRepository = verbRepository;
    }

    @Override
    public boolean bind(Creature actor, String token) {
        Optional<Verb> verbOptional = verbRepository.findFirstByNameIgnoreCaseStartingWith(
            Sort.by(
                Sort.Direction.ASC,
                "priority", "name"),
            token);

        if (!verbOptional.isPresent()) {
            return false;
        }

        if (actor.getRoles().stream().noneMatch(role -> "SUPER".equals(role.getName()))
            && actor.getRoles().stream().noneMatch(actorRole -> verbOptional.get().getRoles().contains(actorRole))) {

            return false;
        }

        this.verb = verbOptional.get();
        return true;
    }

    public Verb getVerb() {
        return verb;
    }
}
