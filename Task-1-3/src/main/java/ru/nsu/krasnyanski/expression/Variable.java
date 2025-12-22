package ru.nsu.krasnyanski.expression;

import java.util.Map;
import ru.nsu.krasnyanski.exception.ExpressionException;
import ru.nsu.krasnyanski.exception.VariableNotAssignedException;

/**
 * Represents variable in expression.
 */
public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public String print() {
        return name;
    }

    @Override
    public Expression derivative(String variable) {
        if (name.equals(variable)) {
            return new Number(1);
        }
        return new Number(0);
    }

    @Override
    public int eval(Map<String, Integer> variables) throws ExpressionException {
        if (!variables.containsKey(name)) {
            throw new VariableNotAssignedException(name);
        }
        return variables.get(name);
    }

    @Override
    public Expression simplify() { return this; }
}
