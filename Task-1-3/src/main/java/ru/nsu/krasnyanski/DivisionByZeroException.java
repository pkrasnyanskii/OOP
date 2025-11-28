package ru.nsu.krasnyanski;

/**
 * Exception thrown when division by zero occurs.
 */
public class DivisionByZeroException extends ExpressionException {
    public DivisionByZeroException() {
        super("Division by zero");
    }
}
