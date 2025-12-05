package ru.nsu.krasnyanski.exception;

/**
 * Exception thrown when a variable is not assigned a value.
 */
public class VariableNotAssignedException extends ExpressionException {
    public VariableNotAssignedException(String variableName) {
        super("Variable not assigned: " + variableName);
    }
}
