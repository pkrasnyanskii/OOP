package ru.nsu.krasnyanski;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.exception.ExpressionException;
import ru.nsu.krasnyanski.exception.InvalidExpressionException;
import ru.nsu.krasnyanski.expression.Expression;
import ru.nsu.krasnyanski.parser.Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for additional tasks:
 * - parsing without mandatory parentheses
 * - expression simplification
 */
class ExpressionExtraTest {

    @Test
    void testParseWithoutParenthesesSimple() throws InvalidExpressionException {
        Expression e = Parser.parseWithoutParentheses("3+2");
        assertEquals("(3+2)", e.print());
    }

    @Test
    void testParseWithoutParenthesesWithPrecedence() throws InvalidExpressionException {
        Expression e = Parser.parseWithoutParentheses("3+2*x");
        assertEquals("(3+(2*x))", e.print());
    }

    @Test
    void testParseWithoutParenthesesMultipleOperators() throws InvalidExpressionException {
        Expression e = Parser.parseWithoutParentheses("x*x+2*x+1");
        assertEquals("(((x*x)+(2*x))+1)", e.print());
    }

    @Test
    void testParseWithoutParenthesesWithParenthesesInside() throws InvalidExpressionException {
        Expression e = Parser.parseWithoutParentheses("4*(x-2)");
        assertEquals("(4*(x-2))", e.print());
    }

    @Test
    void testParseWithoutParenthesesInvalid() {
        assertThrows(InvalidExpressionException.class,
                () -> Parser.parseWithoutParentheses("3+*x"));
    }

    @Test
    void testSimplifyConstantExpression() throws InvalidExpressionException {
        Expression e = Parser.parse("(3+(2*4))");
        Expression simplified = e.simplify();

        assertEquals("11", simplified.print());
        assertEquals("(3+(2*4))", e.print());
    }

    @Test
    void testSimplifyMultiplicationByZero() throws InvalidExpressionException {
        Expression e = Parser.parse("(x*0)");
        Expression simplified = e.simplify();

        assertEquals("0", simplified.print());
    }

    @Test
    void testSimplifyMultiplicationByOne() throws InvalidExpressionException {
        Expression e = Parser.parse("(1*x)");
        Expression simplified = e.simplify();

        assertEquals("x", simplified.print());
    }

    @Test
    void testSimplifySubtractionOfSameExpression() throws InvalidExpressionException {
        Expression e = Parser.parse("(x-x)");
        Expression simplified = e.simplify();

        assertEquals("0", simplified.print());
    }

    @Test
    void testSimplifyNestedExpression() throws InvalidExpressionException {
        Expression e = Parser.parse("((x*1)+(0*y))");
        Expression simplified = e.simplify();

        assertEquals("x", simplified.print());
    }

    @Test
    void testSimplifyDoesNotChangeOriginal() throws InvalidExpressionException {
        Expression e = Parser.parse("(x+0)");
        Expression simplified = e.simplify();

        assertEquals("(x+0)", e.print());
        assertEquals("x", simplified.print());
    }

    @Test
    void testSimplifyWithEvaluationAfter() throws InvalidExpressionException, ExpressionException {
        Expression e = Parser.parse("((2*3)+(x*0))");
        Expression simplified = e.simplify();

        assertEquals("6", simplified.print());
        assertEquals(6, simplified.eval("x=10"));
    }
}
