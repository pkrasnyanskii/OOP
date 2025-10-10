package ru.nsu.krasnyanski.expression;

import java.util.Map;

import ru.nsu.krasnyanski.exception.ExpressionException;
import ru.nsu.krasnyanski.exception.InvalidExpressionException;

import static ru.nsu.krasnyanski.parser.Parser.parseVariables;

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
     * @throws ExpressionException if evaluation fails
     */
    public abstract int eval(Map<String, Integer> variables) throws ExpressionException;

    /**
     * Evaluates expression using string of variable assignments.
     *
     * @param variables string like "x=5;y=3"
     * @return integer result
     * @throws ExpressionException if evaluation fails
     * @throws InvalidExpressionException if variable string invalid
     */
    public int eval(String variables) throws ExpressionException, InvalidExpressionException {
        Map<String, Integer> map = parseVariables(variables);
        return eval(map);
    }
}
