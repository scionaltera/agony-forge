package com.agonyforge.core.model;

import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.delegate.game.binding.BindingDescription;
import com.agonyforge.core.controller.interpret.delegate.game.command.CommandDescription;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static com.agonyforge.core.service.InvokerService.REQUIRED_ARG_COUNT;

@Entity
public class Verb {
    @Id
    private String name;
    private String bean;
    private int priority;
    private boolean quoting = false;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Role> roles = new HashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBean() {
        return bean;
    }

    public void setBean(String bean) {
        this.bean = bean;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isQuoting() {
        return quoting;
    }

    public void setQuoting(boolean quoting) {
        this.quoting = quoting;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public static void showVerbSyntax(Verb verb, Object command, Output output) {
        output.append("[default]Usages for the '" + verb.getName() + "' command:");

        Arrays.stream(ReflectionUtils.getUniqueDeclaredMethods(command.getClass()))
            .filter(method -> "invoke".equals(method.getName()))
            .forEach(method -> {
                StringBuilder buf = new StringBuilder();

                buf.append(verb.getName());
                buf.append(" ");

                if (method.getParameterCount() == REQUIRED_ARG_COUNT) {
                    buf.append("(no arguments)");
                } else {
                    for (int i = REQUIRED_ARG_COUNT; i < method.getParameterCount(); i++) {
                        Class bindingClass = method.getParameterTypes()[i];
                        BindingDescription description = AnnotationUtils.findAnnotation(bindingClass, BindingDescription.class);

                        buf.append("&lt;");
                        buf.append(description == null ? "argument" : description.value());
                        buf.append("&gt;&nbsp;");
                    }
                }

                CommandDescription commandDescription = AnnotationUtils.findAnnotation(method, CommandDescription.class);

                if (commandDescription != null) {
                    buf.append(" - ");
                    buf.append(commandDescription.value());
                }

                output.append(buf.toString());
            });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Verb)) return false;
        Verb verb = (Verb) o;
        return Objects.equals(getName(), verb.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
