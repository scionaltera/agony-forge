package com.agonyforge.core.controller;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PublicControllerTest {
    private PublicController controller;

    @Before
    public void setUp() {
        controller = new PublicController();
    }

    @Test
    public void testPlayController() {
        assertEquals("play", controller.play());
    }
}
