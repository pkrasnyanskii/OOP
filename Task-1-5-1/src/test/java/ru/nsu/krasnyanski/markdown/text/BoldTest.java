package ru.nsu.krasnyanski.markdown.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;


class BoldTest {

    @Test
    void testToMarkdown() {
        Bold b = new Bold("bold");
        assertEquals("**bold**", b.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        Bold b1 = new Bold("x");
        Bold b2 = new Bold("x");
        Bold b3 = new Bold("y");

        assertEquals(b1, b2);
        assertNotEquals(b1, b3);
        assertEquals(b1.hashCode(), b2.hashCode());
    }
}
