package com.agonyforge.core.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Verb {
    @Id
    private String name;
    private String bean;
    private int priority;
    private boolean quoting = false;

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
