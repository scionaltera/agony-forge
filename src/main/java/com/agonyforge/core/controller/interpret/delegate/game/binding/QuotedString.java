package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(scopeName = "prototype")
@BindingDescription("some text")
public class QuotedString implements ArgumentBinding {
    private String text;

    @Override
    public boolean bind(Creature actor, String token) {
        this.text = token;
        return true;
    }

    @Override
    public String getToken() {
        return text;
    }
}
