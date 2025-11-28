package ru.nsu.krasnyanski;

public class VariableNotAssignedException extends ExpressionException {
    public VariableNotAssignedException(String variableName) {
        super("Variable not assigned: " + variableName);
    }
}
