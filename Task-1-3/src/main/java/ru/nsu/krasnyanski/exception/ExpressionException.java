package ru.nsu.krasnyanski.exception;

/**
 * Base exception for evaluation errors in expressions.
 */
public class ExpressionException extends Exception {
    public ExpressionException(String message) {
        super(message);
    }
}
