package com.agonyforge.core.controller.interpret.delegate.game.command;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class HelpCommandTest {
    @Mock
    private VerbRepository verbRepository;

    private Creature creature;
    private Output output;

    private HelpCommand command;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        creature = new Creature();
        output = new Output();

        when(verbRepository.findAll(any(Sort.class))).thenReturn(generateVerbs());

        command = new HelpCommand(verbRepository);
    }

    @Test
    void testInvoke() {
        command.invoke(creature, output);

        assertTrue(output.getOutput().stream().anyMatch(line -> line.contains("All Commands")));

        for (int i = 0; i < 5; i++) {
            String verbName = "verb" + i;

            assertTrue(output.getOutput().stream().anyMatch(line -> line.contains(verbName)));
        }
    }

    private List<Verb> generateVerbs() {
        List<Verb> verbs = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Verb verb = new Verb();

            verb.setName("verb" + i);
            verbs.add(verb);
        }

        return verbs;
    }
}
