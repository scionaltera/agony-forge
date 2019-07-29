package com.agonyforge.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PublicControllerTest {
    private PublicController controller;

    @BeforeEach
    void setUp() {
        controller = new PublicController();
    }

    @Test
    void testPlayController() {
        assertEquals("play", controller.play());
    }
}
