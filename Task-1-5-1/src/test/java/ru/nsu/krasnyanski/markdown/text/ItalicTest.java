package ru.nsu.krasnyanski.markdown.text;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItalicTest {

    @Test
    void testToMarkdown() {
        Italic i = new Italic("ital");
        assertEquals("*ital*", i.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        Italic i1 = new Italic("x");
        Italic i2 = new Italic("x");
        Italic i3 = new Italic("y");

        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
        assertEquals(i1.hashCode(), i2.hashCode());
    }
}
