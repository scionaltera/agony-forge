package com.agonyforge.core.service;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.command.WhoCommand;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class InvokerServiceTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private VerbRepository verbRepository;

    @Mock
    private WhoCommand whoCommand;

    private Creature ch;
    private Output output;

    private InvokerService invokerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        ch = new Creature();
        output = new Output();

        Verb verb = new Verb();

        verb.setName("who");
        verb.setBean("whoCommand");

        when(verbRepository.findFirstByNameIgnoreCaseStartingWith(any(Sort.class), eq("WHO"))).thenReturn(Optional.of(verb));
        when(applicationContext.getBean(eq("whoCommand"))).thenReturn(whoCommand);

        invokerService = new InvokerService(applicationContext, verbRepository);
    }

    @Test
    void testEmptyTokens() {
        invokerService.invoke(ch, output, null, Collections.emptyList());

        verifyZeroInteractions(applicationContext, verbRepository);
    }

    @Test
    void testUnknownVerb() {
        invokerService.invoke(ch, output, "what", Collections.singletonList("WHAT"));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("WHAT"));

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Unknown verb: WHAT")));
    }

    @Test
    void testNoMatchingArguments() {
        invokerService.invoke(ch, output, "who am I", Arrays.asList("WHO", "AM", "I"));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("WHO"));
        verify(whoCommand, never()).invoke(any(), any());

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("No candidate methods matched the number of tokens provided")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Usages for the 'who' command")));
    }

    @Test
    void testMatchingArguments() {
        invokerService.invoke(ch, output, "who", Collections.singletonList("WHO"));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("WHO"));
        verify(whoCommand).invoke(eq(ch), eq(output));
    }
}
