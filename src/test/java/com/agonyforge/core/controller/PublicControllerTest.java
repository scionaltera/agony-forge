package com.agonyforge.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PublicControllerTest {
    private PublicController controller;

    @BeforeEach
    public void setUp() {
        controller = new PublicController();
    }

    @Test
    public void testPlayController() {
        assertEquals("play", controller.play());
    }
}
