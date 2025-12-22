package ru.nsu.krasnyanski.expression;

import java.util.Map;

/**
 * Represents constant number in expression.
 */
public class Number extends Expression {
    private final int value;

    public Number(int value) {
        this.value = value;
    }

    @Override
    public String print() {
        return Integer.toString(value);
    }

    @Override
    public Expression derivative(String variable) {
        return new Number(0);
    }

    @Override
    public int eval(Map<String, Integer> variables) {
        return value;
    }

    @Override
    public Expression simplify() {
        return this;
    }

    public int getValue() {
        return value;
    }
}
