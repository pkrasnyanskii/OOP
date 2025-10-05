package ru.nsu.krasnyanski;

import java.util.Map;

/**
 * Addition expression.
 */
public class Add extends Expression {
    private final Expression left;
    private final Expression right;

    public Add(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return "(" + left.print() + "+" + right.print() + ")";
    }

    @Override
    public Expression derivative(String variable) {
        return new Add(left.derivative(variable), right.derivative(variable));
    }

    @Override
    public int eval(Map<String, Integer> variables) throws ExpressionException {
        return left.eval(variables) + right.eval(variables);
    }
}
