package com.agonyforge.core.model.util;

import javax.persistence.AttributeConverter;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

public abstract class BaseEnumSetConverter<E extends Enum<E> & PersistentEnum> implements AttributeConverter<EnumSet<E>, Long> {
    private final Class<E> klass;

    public BaseEnumSetConverter(Class<E> klass) {
        this.klass = klass;
    }

    @Override
    public Long convertToDatabaseColumn(EnumSet<E> attribute) {
        return attribute
            .stream()
            .mapToLong(constant -> 1L << constant.getIndex())
            .reduce(0L, (total, constant) -> total | constant);
    }

    @Override
    public EnumSet<E> convertToEntityAttribute(Long dbData) {
        return Arrays
            .stream(klass.getEnumConstants())
            .filter(constant -> (dbData & 1L << constant.getIndex()) != 0)
            .collect(Collectors.toCollection(() -> EnumSet.noneOf(klass)));
    }
}
