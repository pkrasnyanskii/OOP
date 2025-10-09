package ru.nsu.krasnyanski.expression;

import ru.nsu.krasnyanski.exception.DivisionByZeroException;
import ru.nsu.krasnyanski.exception.ExpressionException;

import java.util.Map;

/**
 * Division expression.
 */
public class Div extends Expression {
    private final Expression left;
    private final Expression right;

    public Div(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return "(" + left.print() + "/" + right.print() + ")";
    }

    @Override
    public Expression derivative(String variable) {
        return new Div(
                new Sub(
                        new Mul(left.derivative(variable), right),
                        new Mul(left, right.derivative(variable))
                ),
                new Mul(right, right)
        );
    }

    @Override
    public int eval(Map<String, Integer> variables) throws ExpressionException {
        int divisor = right.eval(variables);
        if (divisor == 0) {
            throw new DivisionByZeroException();
        }
        return left.eval(variables) / divisor;
    }
}
