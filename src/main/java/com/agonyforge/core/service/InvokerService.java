package com.agonyforge.core.service;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.command.Description;
import com.agonyforge.core.model.Creature;
import com.agonyforge.core.model.Verb;
import com.agonyforge.core.model.repository.VerbRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class InvokerService {
    private static final int REQUIRED_ARG_COUNT = 2; // every invoke() method requires at least this many arguments

    private ApplicationContext applicationContext;
    private VerbRepository verbRepository;

    @Inject
    public InvokerService(ApplicationContext applicationContext, VerbRepository verbRepository) {
        this.applicationContext = applicationContext;
        this.verbRepository = verbRepository;
    }

    @Transactional
    public void invoke(Creature invoker, Output output, List<String> tokens) {
        if (tokens.isEmpty()) {
            return;
        }

        String verbToken = tokens.get(0);
        Optional<Verb> verbOptional = verbRepository.findFirstByNameIgnoreCaseStartingWith(
            Sort.by(Sort.Direction.ASC, "priority", "name"),
            verbToken);

        if (!verbOptional.isPresent()) {
            output.append("[red]No such verb: " + verbToken);
            return;
        } else {
            output.append("[black]Verb: " + verbToken);
        }

        Object bean = applicationContext.getBean(verbOptional.get().getBean());

        List<Method> candidates = Arrays.stream(ReflectionUtils.getUniqueDeclaredMethods(bean.getClass()))
            .filter(method -> "invoke".equals(method.getName()))
            .filter(method -> REQUIRED_ARG_COUNT + (tokens.size() - 1) == method.getParameterCount())
            .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            output.append("[red]No method matches those arguments.");
            output.append("[default]Usage for the " + verbOptional.get().getName() + " command:");

            Arrays.stream(ReflectionUtils.getUniqueDeclaredMethods(bean.getClass()))
                .filter(method -> "invoke".equals(method.getName()))
                .forEach(method -> {
                    StringBuilder buf = new StringBuilder();

                    buf.append(verbOptional.get().getName());
                    buf.append(" ");

                    if (method.getParameterCount() == REQUIRED_ARG_COUNT) {
                        buf.append("(no arguments)");
                    } else {
                        for (int i = REQUIRED_ARG_COUNT; i < method.getParameterCount(); i++) {
                            buf.append("&lt;");
                            buf.append(method.getParameters()[i]);
                            buf.append("&gt;");
                        }
                    }

                    Description descriptionAnnotation = AnnotationUtils.findAnnotation(method, Description.class);

                    if (descriptionAnnotation != null) {
                        buf.append(" - ");
                        buf.append(descriptionAnnotation.value());
                    }

                    output.append(buf.toString());
                });
        }

        candidates.forEach(candidate -> output.append("[black]Method candidate: " + Arrays.toString(candidate.getParameters())));

        for (Method candidate : candidates) {
            ReflectionUtils.invokeMethod(candidate, bean, Stream.of(invoker, output).toArray());
            return;
        }
    }
}
