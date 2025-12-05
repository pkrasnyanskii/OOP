package ru.nsu.krasnyanski.expression;

import java.util.Map;

import ru.nsu.krasnyanski.exception.ExpressionException;

/**
 * Multiplication expression.
 */
public class Mul extends Expression {
    private final Expression left;
    private final Expression right;

    public Mul(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return "(" + left.print() + "*" + right.print() + ")";
    }

    @Override
    public Expression derivative(String variable) {
        return new Add(new Mul(left.derivative(variable), right),
                new Mul(left, right.derivative(variable)));
    }

    @Override
    public int eval(Map<String, Integer> variables) throws ExpressionException {
        return left.eval(variables) * right.eval(variables);
    }
}
