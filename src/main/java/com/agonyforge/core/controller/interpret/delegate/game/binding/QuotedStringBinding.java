package com.agonyforge.core.controller.interpret.delegate.game.binding;

import com.agonyforge.core.model.Creature;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Scope(scopeName = "prototype")
@BindingDescription("text")
public class QuotedStringBinding implements ArgumentBinding {
    private String text;

    @Override
    public boolean bind(Creature actor, String token) {
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        this.text = token;
        return true;
    }

    public String getToken() {
        return text;
    }
}
