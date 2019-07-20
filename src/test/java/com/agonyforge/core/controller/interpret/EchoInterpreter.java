package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;
import org.springframework.stereotype.Component;

@Component
public class EchoInterpreter extends BaseInterpreter {
    @Override
    public Output interpret(Input input, Connection connection) {
        return new Output("[cyan]" + input).append(prompt(connection));
    }

    @Override
    public Output prompt(Connection connection) {
        return new Output("[default]> ");
    }
}
