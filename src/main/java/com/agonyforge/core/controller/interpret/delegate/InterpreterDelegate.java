package com.agonyforge.core.controller.interpret.delegate;

import com.agonyforge.core.controller.Input;
import com.agonyforge.core.controller.Output;
import com.agonyforge.core.controller.interpret.Interpreter;
import com.agonyforge.core.model.Connection;

public interface InterpreterDelegate {
    Output interpret(Interpreter primary, Input input, Connection connection);
    Output prompt(Interpreter primary, Connection connection);
}
