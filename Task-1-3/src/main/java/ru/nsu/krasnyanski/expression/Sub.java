package ru.nsu.krasnyanski.expression;

import java.util.Map;
import ru.nsu.krasnyanski.exception.ExpressionException;

/**
 * Subtraction expression.
 */
public class Sub extends Expression {
    private final Expression left;
    private final Expression right;

    public Sub(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public String print() {
        return "(" + left.print() + "-" + right.print() + ")";
    }

    @Override
    public Expression derivative(String variable) {
        return new Sub(left.derivative(variable), right.derivative(variable));
    }

    @Override
    public int eval(Map<String, Integer> variables) throws ExpressionException {
        return left.eval(variables) - right.eval(variables);
    }

    @Override
    public Expression simplify() {
        Expression l = left.simplify();
        Expression r = right.simplify();

        if (l.print().equals(r.print())) {
            return new Number(0);
        }

        if (l instanceof Number a && r instanceof Number b) {
            return new Number(a.getValue() - b.getValue());
        }

        if (l instanceof Number a && a.getValue() == 0) {
            return new Mul(new Number(-1), r);
        }

        return new Sub(l, r);
    }

}
