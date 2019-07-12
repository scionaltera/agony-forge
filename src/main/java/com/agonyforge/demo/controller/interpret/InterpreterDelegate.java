package com.agonyforge.demo.controller.interpret;

import com.agonyforge.demo.controller.Input;
import com.agonyforge.demo.controller.Output;
import com.agonyforge.demo.model.Connection;

public interface InterpreterDelegate {
    Output interpret(Interpreter primary, Input input, Connection connection);
    Output prompt(Interpreter primary, Connection connection);
}
