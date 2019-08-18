package com.agonyforge.core.controller.interpret.delegate.creation;

import com.agonyforge.core.model.util.BaseEnumSetConverter;
import com.agonyforge.core.model.util.PersistentEnum;

public enum DefaultCharacterCreationConnectionState implements PersistentEnum {
    DEFAULT(0, "askGender");

    private int index;
    private String property;

    DefaultCharacterCreationConnectionState(int index, String property) {
        this.index = index;
        this.property = property;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public String toProperty() {
        return property;
    }

    public static class Converter extends BaseEnumSetConverter<DefaultCharacterCreationConnectionState> {
        public Converter() {
            super(DefaultCharacterCreationConnectionState.class);
        }
    }
}
