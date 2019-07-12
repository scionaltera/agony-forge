package com.agonyforge.demo.model;

import com.agonyforge.demo.model.util.BaseEnumSetConverter;
import com.agonyforge.demo.model.util.PersistentEnum;

public enum DefaultLoginConnectionState implements PersistentEnum {
    DEFAULT(0, "askNew"),
    LOGIN_ASK_NAME(1, "loginAskName"),
    LOGIN_ASK_PASSWORD(2, "loginAskPassword"),
    CREATE_CHOOSE_NAME(3, "createChooseName"),
    CREATE_CONFIRM_NAME(4, "createConfirmName"),
    CREATE_CHOOSE_PASSWORD(5, "createChoosePassword"),
    CREATE_CONFIRM_PASSWORD(6, "createConfirmPassword"),
    RECONNECT(7, "reconnect");

    private int index;
    private String property;

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
