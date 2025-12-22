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

    @Override
    public Expression simplify() {
        Expression l = left.simplify();
        Expression r = right.simplify();

        if ((l instanceof Number a && a.getValue() == 0)
                || (r instanceof Number b && b.getValue() == 0)) {
            return new Number(0);
        }

        if (l instanceof Number a && a.getValue() == 1) {
            return r;
        }
        if (r instanceof Number b && b.getValue() == 1) {
            return l;
        }

        if (l instanceof Number a && r instanceof Number b) {
            return new Number(a.getValue() * b.getValue());
        }

        return new Mul(l, r);
    }

}
