package com.agonyforge.demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Output {
    private List<String> lines = new ArrayList<>();
    private boolean isSecret = false;

    public Output() {
        // this method intentionally left blank
    }

    public Output(String ... output) {
        append(output);
    }

    public Output(Collection<String> output) {
        append(output);
    }

    public Output(Output... outputs) {
        append(outputs);
    }

    public Output append(String ... output) {
        lines.addAll(Arrays.asList(output));

        return this;
    }

    public Output append(Collection<String> output) {
        lines.addAll(output);

        return this;
    }

    public Output append(Output... outputs) {
        Arrays.stream(outputs).forEach(output -> this.lines.addAll(output.lines));

        setSecret(Arrays.stream(outputs).anyMatch(Output::getSecret));

        return this;
    }

    public Output setSecret(boolean isSecret) {
        this.isSecret = isSecret;

        return this;
    }

    public boolean getSecret() {
        return isSecret;
    }

    public List<String> getOutput() {
        return toList();
    }

    public List<String> toList() {
        return new ArrayList<>(lines);
    }

    @Override
    public String toString() {
        return String.join("\n", lines);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Output)) return false;
        Output output = (Output) o;
        return lines.equals(output.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lines);
    }
}
