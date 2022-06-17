package com.agonyforge.core.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.ui.Model;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

class CustomErrorControllerTest {
    @Mock
    private ErrorAttributes errorAttributes;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    private CustomErrorController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        when(errorAttributes.getErrorAttributes(any(), anyBoolean())).thenReturn(Collections.emptyMap());

        controller = new CustomErrorController(errorAttributes);
    }

    @Test
    void testError() {
        String view = controller.error(model, request);

        assertEquals("error", view);
    }
}
