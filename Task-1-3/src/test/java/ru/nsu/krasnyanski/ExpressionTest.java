package ru.nsu.krasnyanski;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.exception.DivisionByZeroException;
import ru.nsu.krasnyanski.exception.ExpressionException;
import ru.nsu.krasnyanski.exception.InvalidExpressionException;
import ru.nsu.krasnyanski.exception.VariableNotAssignedException;
import ru.nsu.krasnyanski.expression.*;
import ru.nsu.krasnyanski.expression.Number;
import ru.nsu.krasnyanski.parser.Parser;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Expression hierarchy and Parser.
 */
class ExpressionTest {

    @Test
    void testNumberPrintAndEval() throws ExpressionException {
        Expression n = new ru.nsu.krasnyanski.expression.Number(7);
        assertEquals("7", n.print());
        assertEquals(7, n.eval(Map.of()));
        assertEquals("0", n.derivative("x").print());
    }

    @Test
    void testVariablePrintEvalDerivative() throws ExpressionException {
        Expression v = new Variable("x");
        assertEquals("x", v.print());
        assertEquals(5, v.eval(Map.of("x", 5)));
        assertEquals("1", v.derivative("x").print());
        assertEquals("0", v.derivative("y").print());
    }

    @Test
    void testAdd() throws ExpressionException {
        Expression e = new Add(new ru.nsu.krasnyanski.expression.Number(2), new Variable("x"));
        assertEquals("(2+x)", e.print());
        assertEquals(12, e.eval(Map.of("x", 10)));
        Expression d = e.derivative("x");
        assertEquals("(0+1)", d.print());
    }

    @Test
    void testSub() throws ExpressionException {
        Expression e = new Sub(new ru.nsu.krasnyanski.expression.Number(5), new Variable("y"));
        assertEquals("(5-y)", e.print());
        assertEquals(2, e.eval(Map.of("y", 3)));
        Expression d = e.derivative("y");
        assertEquals("(0-1)", d.print());
    }

    @Test
    void testMul() throws ExpressionException {
        Expression e = new Mul(new Variable("x"), new ru.nsu.krasnyanski.expression.Number(4));
        assertEquals("(x*4)", e.print());
        assertEquals(20, e.eval(Map.of("x", 5)));
        Expression d = e.derivative("x");
        assertEquals("((1*4)+(x*0))", d.print());
    }

    @Test
    void testDiv() throws ExpressionException {
        Expression e = new Div(new Variable("x"), new ru.nsu.krasnyanski.expression.Number(2));
        assertEquals("(x/2)", e.print());
        assertEquals(3, e.eval(Map.of("x", 6)));
        Expression d = e.derivative("x");
        assertEquals("(((1*2)-(x*0))/(2*2))", d.print());
    }

    @Test
    void testParserSimple() throws ExpressionException, InvalidExpressionException {
        Expression e = Parser.parse("(3+5)");
        assertEquals("(3+5)", e.print());
        assertEquals(8, e.eval(Map.of()));
    }

    @Test
    void testParserNested() throws ExpressionException, InvalidExpressionException {
        Expression e = Parser.parse("(3+(2*x))");
        assertEquals("(3+(2*x))", e.print());
        assertEquals(23, e.eval(Map.of("x", 10)));
    }

    @Test
    void testParserWhitespace() throws InvalidExpressionException {
        Expression e = Parser.parse(" ( 4 * ( x - 2 ) ) ");
        assertEquals("(4+(x-2))".replace('+', '*'), e.print());
    }

    @Test
    void testParserThrowsOnMalformed() {
        assertThrows(InvalidExpressionException.class, () -> Parser.parse("(3+)"));
        assertThrows(InvalidExpressionException.class, () -> Parser.parse(""));
    }

    @Test
    void testEvalStringInput() throws ExpressionException, InvalidExpressionException {
        Expression e = Parser.parse("(x+3)");
        assertEquals(8, e.eval("x=5"));
        assertEquals(7, e.eval("x=4"));
    }

    @Test
    void testVariableNotAssignedException() throws InvalidExpressionException {
        Expression v = new Variable("y");
        assertThrows(VariableNotAssignedException.class, () -> v.eval(Map.of()));
    }

    @Test
    void testDivisionByZeroException() throws InvalidExpressionException, ExpressionException {
        Expression e = new Div(new ru.nsu.krasnyanski.expression.Number(10), new Number(0));
        assertThrows(DivisionByZeroException.class, () -> e.eval(Map.of()));
    }

    @Test
    void testInvalidExpressionExceptionParse() {
        assertThrows(InvalidExpressionException.class, () -> Parser.parse("(3+)"));
        assertThrows(InvalidExpressionException.class, () -> Parser.parse(")5("));
        assertThrows(InvalidExpressionException.class, () -> Parser.parse(""));
    }
}
