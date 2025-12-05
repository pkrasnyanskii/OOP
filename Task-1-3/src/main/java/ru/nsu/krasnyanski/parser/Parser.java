package ru.nsu.krasnyanski.parser;

import ru.nsu.krasnyanski.exception.InvalidExpressionException;
import ru.nsu.krasnyanski.expression.*;
import ru.nsu.krasnyanski.expression.Number;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser for expressions.
 * Supports numbers, variables, and operators (+, -, *, /) with parentheses.
 */
public class Parser {

    /**
     * Parses a string into an Expression.
     *
     * @param input expression string like "(3+(2*x))"
     * @return Expression tree
     * @throws InvalidExpressionException if string invalid
     */
    public static Expression parse(String input) throws InvalidExpressionException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidExpressionException("Empty expression");
        }
        return new ParserImpl(input.replaceAll("\\s+", "")).parse();
    }

    private static class ParserImpl {
        private final String expressionStr;
        private int pos = 0;

        ParserImpl(String expressionStr) {
            this.expressionStr = expressionStr;
        }

        Expression parse() throws InvalidExpressionException {
            Expression expression = parseExpression();
            if (pos != expressionStr.length()) {
                throw new InvalidExpressionException(
                        "Unexpected input: " + expressionStr.substring(pos)
                );
            }
            return expression;
        }

        private Expression parseExpression() throws InvalidExpressionException {
            if (pos >= expressionStr.length()) {
                throw new InvalidExpressionException("Unexpected end of input");
            }

            char ch = expressionStr.charAt(pos);

            if (Character.isDigit(ch)) {
                return parseNumber();
            } else if (Character.isLetter(ch)) {
                return parseVariable();
            } else if (ch == '(') {
                pos++;
                Expression left = parseExpression();

                if (pos >= expressionStr.length()) {
                    throw new InvalidExpressionException("Missing operator at position " + pos);
                }

                char operator = expressionStr.charAt(pos++);
                Expression right = parseExpression();

                if (pos >= expressionStr.length() || expressionStr.charAt(pos) != ')') {
                    throw new InvalidExpressionException(
                            "Missing closing parenthesis at position " + pos
                    );
                }

                pos++;
                return switch (operator) {
                    case '+' -> new Add(left, right);
                    case '-' -> new Sub(left, right);
                    case '*' -> new Mul(left, right);
                    case '/' -> new Div(left, right);
                    default -> throw new InvalidExpressionException("Unknown operator: " + operator);
                };
            } else {
                throw new InvalidExpressionException("Unexpected char: " + ch + " at " + pos);
            }
        }

        private Expression parseNumber() {
            int start = pos;
            while (pos < expressionStr.length() && Character.isDigit(expressionStr.charAt(pos))) {
                pos++;
            }
            return new Number(Integer.parseInt(expressionStr.substring(start, pos)));
        }

        private Expression parseVariable() {
            int start = pos;
            while (pos < expressionStr.length() && Character.isLetter(expressionStr.charAt(pos))) {
                pos++;
            }
            return new Variable(expressionStr.substring(start, pos));
        }
    }

    /**
     * Parses variable assignments from string.
     *
     * @param s string like "x=5;y=3"
     * @return map of variable → value
     * @throws InvalidExpressionException if string malformed
     */
    public static Map<String, Integer> parseVariables(String s) throws InvalidExpressionException {
        Map<String, Integer> map = new HashMap<>();
        if (s == null || s.trim().isEmpty()) {
            return map;
        }

        String[] assignments = s.split(";");
        for (String assignment : assignments) {
            String[] kv = assignment.split("=");
            if (kv.length != 2) {
                throw new InvalidExpressionException("Invalid assignment: " + assignment);
            }

            String key = kv[0].trim();
            int value;

            try {
                value = Integer.parseInt(kv[1].trim());
            } catch (NumberFormatException e) {
                throw new InvalidExpressionException("Invalid number: " + kv[1].trim());
            }

            map.put(key, value);
        }

        return map;
    }
}
