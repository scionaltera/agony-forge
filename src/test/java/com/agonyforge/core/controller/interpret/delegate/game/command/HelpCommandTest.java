package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.binding.VerbBinding;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Role;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class HelpCommandTest {
    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private VerbRepository verbRepository;

    @Mock
    private VerbBinding verbBinding;

    private Creature creature;
    private Output output;
    private Verb verb;

    private HelpCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        creature = new Creature();
        output = new Output();
        verb = new Verb();

        creature.getRoles().add(new Role("PLAYER"));

        verb.setName("help");
        verb.setBean("helpCommand");

        command = new HelpCommand(applicationContext, verbRepository);

        when(verbRepository.findAll(any(Sort.class))).thenReturn(generateVerbs());
        when(verbBinding.getVerb()).thenReturn(verb);
        when(applicationContext.getBean(eq("helpCommand"))).thenReturn(command);
    }

    @Test
    void testInvokeGeneral() {
        command.invoke(creature, output);

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Available Commands")));

        for (int i = 0; i < 5; i++) {
            String verbName = "verb" + i;

            assertTrue(output.getOutput().stream().anyMatch(line -> line.contains(verbName)));
        }
    }

    @Test
    void testInvokeSpecific() {
        command.invoke(creature, output, verbBinding);

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("Usages for the 'help' command")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("help &lt;command&gt;")));
        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("help (no arguments)")));
    }

    private List<Verb> generateVerbs() {
        List<Verb> verbs = new ArrayList<>();
        Role playerRole = new Role("PLAYER");

        for (int i = 0; i < 5; i++) {
            Verb verb = new Verb();

            verb.setName("verb" + i);
            verb.getRoles().add(playerRole);
            verbs.add(verb);
        }

        return verbs;
    }
}
