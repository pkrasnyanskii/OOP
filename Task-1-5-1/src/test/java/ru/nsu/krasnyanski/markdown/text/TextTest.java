package ru.nsu.krasnyanski.markdown.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class TextTest {

    @Test
    void testToMarkdown() {
        Text t = new Text("hello");
        assertEquals("hello", t.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        Text t1 = new Text("a");
        Text t2 = new Text("a");
        Text t3 = new Text("b");

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
