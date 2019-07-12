package com.agonyforge.demo.controller.greeting;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class GreetingLoaderTest {
    private GreetingLoader loader;

    @Before
    public void setUp() {
        loader = new GreetingLoader();
    }

    @Test
    public void testLoad() {
        List<String> greeting = loader.load();

        assertEquals("[yellow]Hello&nbsp;world!", greeting.get(0));
        assertEquals("[yellow]Hello world!", greeting.get(1));
    }
}
