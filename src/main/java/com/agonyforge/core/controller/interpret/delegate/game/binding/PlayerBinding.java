package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.repository.CreatureRepository;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope(scopeName = "prototype")
@BindingDescription("player name")
public class PlayerBinding implements ArgumentBinding {
    private CreatureRepository creatureRepository;
    private Creature player;

    @Inject
    public PlayerBinding(CreatureRepository creatureRepository) {
        this.creatureRepository = creatureRepository;
    }

    @Override
    public boolean bind(Creature actor, String token) {
        Optional<Creature> playerOptional = creatureRepository
            .findByNameAndConnectionIsNotNull(token);

        if (playerOptional.isPresent()) {
            player = playerOptional.get();
            return true;
        }

        return false;
    }

    public Creature getPlayer() {
        return player;
    }
}
