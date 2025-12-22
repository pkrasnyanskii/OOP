package ru.nsu.krasnyanski.markdown.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class InlineCodeTest {

    @Test
    void testToMarkdown() {
        InlineCode c = new InlineCode("code");
        assertEquals("`code`", c.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        InlineCode c1 = new InlineCode("x");
        InlineCode c2 = new InlineCode("x");
        InlineCode c3 = new InlineCode("y");

        assertEquals(c1, c2);
        assertNotEquals(c1, c3);
        assertEquals(c1.hashCode(), c2.hashCode());
    }
}
