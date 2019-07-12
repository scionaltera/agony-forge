package com.agonyforge.demo.controller.interpret;

import com.agonyforge.demo.controller.Input;
import com.agonyforge.demo.controller.Output;
import com.agonyforge.demo.model.Connection;
import com.agonyforge.demo.model.Creature;

public interface Interpreter {
    Output interpret(Input input, Connection connection);
    Output prompt(Connection connection);
    void echo(Creature target, Output message);
    void echoToWorld(Output message, Creature... exclude);
}
