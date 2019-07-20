package com.agonyforge.core.controller.interpret;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.model.Connection;

public interface Interpreter {
    Output interpret(Input input, Connection connection);
    Output prompt(Connection connection);
}
