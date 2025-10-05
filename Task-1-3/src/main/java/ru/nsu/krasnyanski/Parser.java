package ru.nsu.krasnyanski;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser for expressions.
 * Supports numbers, variables, and operators (+, -, *, /) with parentheses.
 */
public class Parser {

    public static Expression parse(String input) throws InvalidExpressionException {
        if (input == null || input.trim().isEmpty()) {
            throw new InvalidExpressionException("Empty expression");
        }
        return new ParserImpl(input.replaceAll("\\s+", "")).parse();
    }

    private static class ParserImpl {
        private final String s;
        private int pos = 0;

        ParserImpl(String s) {
            this.s = s;
        }

        Expression parse() throws InvalidExpressionException {
            Expression e = parseExpression();
            if (pos != s.length()) {
                throw new InvalidExpressionException("Unexpected input: " + s.substring(pos));
            }
            return e;
        }

        private Expression parseExpression() throws InvalidExpressionException {
            if (pos >= s.length()) {
                throw new InvalidExpressionException("Unexpected end of input");
            }

            char ch = s.charAt(pos);

            if (Character.isDigit(ch)) {
                return parseNumber();
            } else if (Character.isLetter(ch)) {
                return parseVariable();
            } else if (ch == '(') {
                pos++;
                Expression left = parseExpression();
                if (pos >= s.length()) {
                    throw new InvalidExpressionException("Missing operator at position " + pos);
                }
                char op = s.charAt(pos++);
                Expression right = parseExpression();
                if (pos >= s.length() || s.charAt(pos) != ')') {
                    throw new InvalidExpressionException("Missing closing parenthesis at position " + pos);
                }
                pos++;
                return switch (op) {
                    case '+' -> new Add(left, right);
                    case '-' -> new Sub(left, right);
                    case '*' -> new Mul(left, right);
                    case '/' -> new Div(left, right);
                    default -> throw new InvalidExpressionException("Unknown operator: " + op);
                };
            } else {
                throw new InvalidExpressionException("Unexpected char: " + ch + " at " + pos);
            }
        }

        private Expression parseNumber() {
            int start = pos;
            while (pos < s.length() && Character.isDigit(s.charAt(pos))) {
                pos++;
            }
            return new Number(Integer.parseInt(s.substring(start, pos)));
        }

        private Expression parseVariable() {
            int start = pos;
            while (pos < s.length() && Character.isLetter(s.charAt(pos))) {
                pos++;
            }
            return new Variable(s.substring(start, pos));
        }
    }

    public static Map<String, Integer> parseVariables(String s) throws InvalidExpressionException {
        Map<String, Integer> map = new HashMap<>();
        if (s == null || s.trim().isEmpty()) return map;

        String[] parts = s.split(";");
        for (String p : parts) {
            String[] kv = p.split("=");
            if (kv.length != 2) {
                throw new InvalidExpressionException("Invalid assignment: " + p);
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
