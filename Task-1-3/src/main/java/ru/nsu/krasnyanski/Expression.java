package ru.nsu.krasnyanski;

import java.util.Map;

/**
 * Abstract class for mathematical expressions.
 */
public abstract class Expression {
    /**
     * Prints the expression in canonical form.
     *
     * @return string representation
     */
    public abstract String print();

    /**
     * Differentiates the expression by variable.
     *
     * @param variable name of variable
     * @return new Expression which is derivative
     */
    public abstract Expression derivative(String variable);

    /**
     * Evaluates expression using variable values.
     *
     * @param variables map variable → integer value
     * @return integer result
     */
    public abstract int eval(Map<String, Integer> variables);
}
