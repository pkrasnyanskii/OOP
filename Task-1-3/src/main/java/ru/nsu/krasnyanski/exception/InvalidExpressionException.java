package ru.nsu.krasnyanski.exception;

/**
 * Exception thrown when an expression is invalid.
 */
public class InvalidExpressionException extends Exception {
    public InvalidExpressionException(String message) {
        super(message);
    }
}
