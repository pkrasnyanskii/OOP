package ru.nsu.krasnyanski;

/**
 * Parser for expressions.
 * Supports numbers, variables, and operators (+, -, *, /) with parentheses.
 */
public class Parser {

    /**
     * Parses a string into an Expression.
     *
     * @param input expression string like (3+(2*x))
     * @return Expression tree
     */
    public static Expression parse(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Empty expression");
        }
        return new ParserImpl(input.replaceAll("\\s+", "")).parse();
    }

    private static class ParserImpl {
        private final String s;
        private int pos = 0;

        ParserImpl(String s) {
            this.s = s;
        }

        Expression parse() {
            Expression e = parseExpression();
            if (pos != s.length()) {
                throw new IllegalArgumentException("Unexpected input: " + s.substring(pos));
            }
            return e;
        }

        private Expression parseExpression() {
            if (pos >= s.length()) {
                throw new IllegalArgumentException("Unexpected end of input");
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
                    throw new IllegalArgumentException("Missing operator at position " + pos);
                }
                char op = s.charAt(pos++);
                Expression right = parseExpression();
                if (pos >= s.length() || s.charAt(pos) != ')') {
                    throw new IllegalArgumentException("Missing closing parenthesis at position " + pos);
                }
                pos++;
                if (op == '+') {
                    return new Add(left, right);
                } else if (op == '-') {
                    return new Sub(left, right);
                } else if (op == '*') {
                    return new Mul(left, right);
                } else if (op == '/') {
                    return new Div(left, right);
                } else {
                    throw new IllegalArgumentException("Unknown operator: " + op);
                }
            } else {
                throw new IllegalArgumentException("Unexpected char: " + ch + " at " + pos);
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
}
