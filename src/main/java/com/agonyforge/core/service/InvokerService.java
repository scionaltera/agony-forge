package com.agonyforge.core.service;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.binding.ArgumentBinding;
import com.agonyforge.core.controller.interpret.delegate.game.binding.BindingDescription;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import com.agonyforge.core.util.FormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.agonyforge.core.model.Role.SUPER_ROLE;

@Component
public class InvokerService {
    public static final int REQUIRED_ARG_COUNT = 2; // every invoke() method requires at least this many arguments

    private static final Logger LOGGER = LoggerFactory.getLogger(InvokerService.class);

    private ApplicationContext applicationContext;
    private VerbRepository verbRepository;

    @Inject
    public InvokerService(ApplicationContext applicationContext, VerbRepository verbRepository) {
        this.applicationContext = applicationContext;
        this.verbRepository = verbRepository;
    }

    @Transactional
    public void invoke(Creature ch, Output output, String raw, List<String> tokens) {
        if (tokens.isEmpty()) {
            LOGGER.error("Invoked with empty token list");
            return;
        }

        // Our grammar always requires the first word to be a verb.
        // The verb is a mapping between a token and a Command object. There can be multiple mappings.
        // For instance "hit", "kill" and "murder" could all map to the same Command, and run the same code.
        String verbToken = tokens.get(0);
        Optional<Verb> verbOptional = verbRepository.findFirstByNameIgnoreCaseStartingWith(
            Sort.by(Sort.Direction.ASC, "priority", "name"),
            verbToken);

        if (!verbOptional.isPresent()) {
            output.append("[red]Unknown verb: " + verbToken);
            return;
        } else {
            output.append("[black]Found verb: " + verbOptional.get().getName() + (verbOptional.get().isQuoting() ? " (quoting)" : ""));
        }

        if (ch.getRoles().stream().noneMatch(role -> SUPER_ROLE.equals(role.getName())) && verbOptional.get().getRoles().stream().noneMatch(role -> ch.getRoles().contains(role))) {
            LOGGER.warn("{} has no role to permit use of verb: {}", ch.getName(), verbOptional.get().getName());
            output.append(String.format("[red]%s has no role to permit use of verb: %s", ch.getName(), verbOptional.get().getName()));
            return;
        } else {
            output.append("[black]Authorization granted for verb: " + verbOptional.get().getName());
        }

        if (verbOptional.get().isQuoting()) { // "quoting" verbs automatically enquote everything after the verb into a single token
            tokens.clear();
            tokens.add(verbToken);
            tokens.add(FormatUtil.removeFirstWord(raw));
        }

        Object command = applicationContext.getBean(verbOptional.get().getBean());

        output.append("[black]Found command: " + command.getClass().getName());

        // The command object should have one or more invoke() methods on it, with at least REQUIRED_ARG_COUNT
        // arguments. Additional arguments must be implementations of ArgumentBinding. The following uses
        // reflection to find all the methods that could be matches.
        List<Method> candidates = Arrays.stream(ReflectionUtils.getUniqueDeclaredMethods(command.getClass()))
            .filter(method -> "invoke".equals(method.getName()))
            .filter(method -> REQUIRED_ARG_COUNT + (tokens.size() - 1) == method.getParameterCount())
            .collect(Collectors.toList());

        // We found no methods that matched our criteria. Print an error message and exit.
        if (candidates.isEmpty()) {
            output.append("[red]No candidate methods matched the number of tokens provided.");
            Verb.showVerbSyntax(verbOptional.get(), command, output);
            return;
        }

        // Debug output to show all the possible matches we found
        candidates.forEach(candidate -> output.append("[black]Found method candidate: " + Arrays.toString(candidate.getParameters())));

        // For each method candidate, see if we can successfully call bind() on all the ArgumentBindings
        for (Method method : candidates) {
            List<ArgumentBinding> argumentBindings = new ArrayList<>();
            boolean isBindingSuccessful = true;

            for (int i = REQUIRED_ARG_COUNT; i < method.getParameterCount(); i++) {
                ArgumentBinding binding = (ArgumentBinding) applicationContext.getBean(method.getParameterTypes()[i]);

                if (binding.bind(ch, tokens.get(i - REQUIRED_ARG_COUNT + 1))) {
                    argumentBindings.add(binding);
                } else {
                    isBindingSuccessful = false;
                    BindingDescription description = AnnotationUtils.findAnnotation(binding.getClass(), BindingDescription.class);
                    output.append(String.format("No \"%s\" found.",
                        description == null ? "object binding" : description.value()));
                }
            }

            if (isBindingSuccessful) {
                // We bound all the tokens to objects, so we can invoke the method.
                ReflectionUtils.invokeMethod(
                    method,
                    command,
                    Stream
                        .concat(Stream.of(ch, output), argumentBindings.stream())
                        .toArray());

                // We're done!
                return;
            } else {
                // We failed to bind one or more tokens, so print some debug info and continue.
                StringBuilder buf = new StringBuilder();

                buf.append(verbOptional.get().getName());
                buf.append(" ");

                for (int i = REQUIRED_ARG_COUNT; i < method.getParameterCount(); i++) {
                    BindingDescription description = AnnotationUtils.findAnnotation(method.getParameterTypes()[i], BindingDescription.class);

                    buf.append("&lt;");
                    buf.append(description == null ? "argument" : description.value());
                    buf.append("&gt;");
                }

                output.append("Could not resolve all arguments for grammar:");
                output.append(buf.toString());
            }
        }

        // We failed to invoke any of the methods! Show the usage and help the player out.
        output.append("[red]Could not successfully bind all arguments to any of the available methods.");
        Verb.showVerbSyntax(verbOptional.get(), command, output);
    }
}
