package com.agonyforge.core.model;

import com.agonyforge.core.model.util.BaseEnumSetConverter;
import com.agonyforge.core.model.util.PersistentEnum;

public enum PortalFlag implements PersistentEnum {
    ZONE_PORTAL(0, "zone portal");

    private int index;
    private String description;

    PortalFlag(int index, String description) {
        this.index = index;
        this.description = description;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public String getDescription() {
        return description;
    }

    public static class Converter extends BaseEnumSetConverter<PortalFlag> {
        public Converter() {
            super(PortalFlag.class);
        }
    }
}
