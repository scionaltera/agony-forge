package com.agonyforge.core.service;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.binding.QuotedString;
import com.agonyforge.core.controller.interpret.delegate.game.binding.VerbBinding;
import com.agonyforge.core.controller.interpret.delegate.game.command.GossipCommand;
import com.agonyforge.core.controller.interpret.delegate.game.command.HelpCommand;
import com.agonyforge.core.controller.interpret.delegate.game.command.WhoCommand;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Role;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Captor
    private ArgumentCaptor<QuotedString> quotedStringCaptor;

    private Creature ch;
    private Output output;
    private Verb verb;

    private InvokerService invokerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        ch = new Creature();
        output = new Output();
        verb = new Verb();

        ch.getRoles().add(new Role("PLAYER"));

        verb.setName("who");
        verb.setBean("whoCommand");
        verb.getRoles().add(new Role("PLAYER"));

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

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Huh?")));
    }

    @Test
    void testNoMatchingArguments() {
        invokerService.invoke(ch, output, "who am I", Arrays.asList("WHO", "AM", "I"));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("WHO"));
        verify(whoCommand, never()).invoke(any(), any());

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Usages for the 'who' command")));
    }

    @Test
    void testMatchingArguments() {
        invokerService.invoke(ch, output, "who", Collections.singletonList("WHO"));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("WHO"));
        verify(whoCommand).invoke(eq(ch), eq(output));
    }

    @Test
    void testNoValidRole() {
        verb.getRoles().clear();
        verb.getRoles().add(new Role("FANCY"));

        invokerService.invoke(ch, output, "who", Collections.singletonList("WHO"));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("WHO"));
        verify(whoCommand, never()).invoke(eq(ch), eq(output));
    }

    @Test
    void testQuotingVerb() {
        GossipCommand gossipCommand = mock(GossipCommand.class);

        when(applicationContext.getBean(eq("gossipCommand"))).thenReturn(gossipCommand);
        when(applicationContext.getBean(eq(QuotedString.class))).thenAnswer(i -> new QuotedString());

        verb.setQuoting(true);
        verb.setBean("gossipCommand");

        invokerService.invoke(ch, output, "who do you think you are?", Stream
            .of("WHO", "DO", "YOU", "THINK", "YOU", "ARE")
            .collect(Collectors.toList()));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("WHO"));
        verify(gossipCommand).invoke(eq(ch), eq(output), quotedStringCaptor.capture());

        QuotedString quotedString = quotedStringCaptor.getValue();

        assertEquals("do you think you are?", quotedString.getToken());
    }

    @Test
    void testFailedBinding() {
        HelpCommand helpCommand = mock(HelpCommand.class);

        when(verbRepository.findFirstByNameIgnoreCaseStartingWith(any(Sort.class), eq("HELP"))).thenReturn(Optional.of(verb));
        when(applicationContext.getBean(eq("helpCommand"))).thenReturn(helpCommand);
        when(applicationContext.getBean(eq(VerbBinding.class))).thenAnswer(i -> new VerbBinding(verbRepository));

        verb.setBean("helpCommand");

        invokerService.invoke(ch, output, "help me", Stream.of("HELP", "ME").collect(Collectors.toList()));

        verify(verbRepository).findFirstByNameIgnoreCaseStartingWith(any(), eq("HELP"));
        verify(helpCommand, never()).invoke(eq(ch), eq(output));
    }
}
