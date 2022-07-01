package com.agonyforge.core.controller.interpret.delegate.login;

import com.agonyforge.core.model.util.BaseEnumSetConverter;
import com.agonyforge.core.model.util.PersistentEnum;

public enum DefaultLoginConnectionState implements PersistentEnum {
    DEFAULT(0, "askNew"),
    LOGIN_ASK_NAME(1, "loginAskName"),
    CREATE_CHOOSE_NAME(2, "createChooseName"),
    CREATE_CONFIRM_NAME(3, "createConfirmName"),
    RECONNECT(4, "reconnect");

    private final int index;
    private final String property;

    DefaultLoginConnectionState(int index, String property) {
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

    public static class Converter extends BaseEnumSetConverter<DefaultLoginConnectionState> {
        public Converter() {
            super(DefaultLoginConnectionState.class);
        }
    }
}
