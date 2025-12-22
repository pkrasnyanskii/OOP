package ru.nsu.krasnyanski.markdown.text;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class StrikeTest {

    @Test
    void testToMarkdown() {
        Strike s = new Strike("strike");
        assertEquals("~~strike~~", s.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        Strike s1 = new Strike("x");
        Strike s2 = new Strike("x");
        Strike s3 = new Strike("y");

        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertEquals(s1.hashCode(), s2.hashCode());
    }
}
