package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;

public abstract class BaseInterpreter implements Interpreter {
    @Override
    public abstract Output interpret(Input input, Connection connection);

    @Override
    public abstract Output prompt(Connection connection);
}
