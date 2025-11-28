package ru.nsu.krasnyanski;

public class DivisionByZeroException extends ExpressionException {
    public DivisionByZeroException() {
        super("Division by zero");
    }
}
