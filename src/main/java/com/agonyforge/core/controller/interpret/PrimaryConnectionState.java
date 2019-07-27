package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.model.util.BaseEnumSetConverter;
import com.agonyforge.core.model.util.PersistentEnum;

public enum PrimaryConnectionState implements PersistentEnum {
    LOGIN(0),
    CREATION(1),
    IN_GAME(2),
    MENU(3),
    DISCONNECTED(4);

    private int index;

    PrimaryConnectionState(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public static class Converter extends BaseEnumSetConverter<PrimaryConnectionState> {
        public Converter() {
            super(PrimaryConnectionState.class);
        }
    }
}
