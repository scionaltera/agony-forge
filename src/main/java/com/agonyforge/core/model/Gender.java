package com.agonyforge.core.model;

import com.agonyforge.core.model.util.BaseEnumSetConverter;
import com.agonyforge.core.model.util.PersistentEnum;

public enum Gender implements PersistentEnum {
    OBJECT(0, "object", "it", "it", "its", "its", "itself"),
    MALE(1, "male", "he", "him", "his", "his", "himself"),
    FEMALE(2, "female", "she", "her", "her", "hers", "herself"),
    NEUTRAL(3, "neutral", "they", "them", "their", "theirs", "themselves");

    private int index;
    private String name;
    private String subject;
    private String object;
    private String possessiveAdjective;
    private String possessivePronoun;
    private String reflexivePronoun;

    Gender(
        int index,
        String name,
        String subject,
        String object,
        String possessiveAdjective,
        String possessivePronoun,
        String reflexivePronoun) {

        this.index = index;
        this.name = name;
        this.subject = subject;
        this.object = object;
        this.possessiveAdjective = possessiveAdjective;
        this.possessivePronoun = possessivePronoun;
        this.reflexivePronoun = reflexivePronoun;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPossessiveAdjective() {
        return possessiveAdjective;
    }

    public String getPossessivePronoun() {
        return possessivePronoun;
    }

    public String getReflexivePronoun() {
        return reflexivePronoun;
    }

    public static class Converter extends BaseEnumSetConverter<Gender> {
        public Converter() {
            super(Gender.class);
        }
    }
}
