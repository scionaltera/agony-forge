package com.agonyforge.core.controller.greeting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GreetingLoaderTest {
    private GreetingLoader loader;

    @BeforeEach
    void setUp() {
        loader = new GreetingLoader();
    }

    @Test
    void testLoad() {
        List<String> greeting = loader.load();

        assertEquals("[yellow]Hello&nbsp;world!", greeting.get(0));
        assertEquals("[yellow]Hello world!", greeting.get(1));
    }
}
