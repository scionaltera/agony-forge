package com.agonyforge.core.model.util;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class BaseEnumSetConverterTest {
    private Converter converter = new Converter();

    @Test
    void testPersist() {
        EnumSet<TestPersistentEnum> testEnumSet = EnumSet.of(
            TestPersistentEnum.ABLE,
            TestPersistentEnum.EASY);

        long result = converter.convertToDatabaseColumn(testEnumSet);

        assertEquals(0b10001, result);
    }

    @Test
    void testRestore() {
        EnumSet<TestPersistentEnum> result = converter.convertToEntityAttribute((long)0b10001);

        assertTrue(result.contains(TestPersistentEnum.ABLE));
        assertFalse(result.contains(TestPersistentEnum.BAKER));
        assertFalse(result.contains(TestPersistentEnum.CHARLIE));
        assertFalse(result.contains(TestPersistentEnum.DOG));
        assertTrue(result.contains(TestPersistentEnum.EASY));

    }

    public static class Converter extends BaseEnumSetConverter<TestPersistentEnum> {
        Converter() {
            super(TestPersistentEnum.class);
        }
    }
}
