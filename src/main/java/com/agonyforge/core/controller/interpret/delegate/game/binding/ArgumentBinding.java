package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;

public interface ArgumentBinding {
    boolean bind(Creature actor, String token);
    String getToken();
}
