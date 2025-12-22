package ru.nsu.krasnyanski.markdown.structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HeadingTest {

    @Test
    void testToMarkdown() {
        Heading h1 = new Heading(1, "Title");
        Heading h3 = new Heading(3, "Subtitle");
        assertEquals("# Title", h1.toMarkdown());
        assertEquals("### Subtitle", h3.toMarkdown());
    }

    @Test
    void testInvalidLevels() {
        assertThrows(MarkdownException.class, () -> new Heading(0, "Too low"));
        assertThrows(MarkdownException.class, () -> new Heading(7, "Too high"));
    }

    @Test
    void testEqualsAndHashCode() {
        Heading h1a = new Heading(1, "A");
        Heading h1b = new Heading(1, "A");
        Heading h1c = new Heading(1, "B");

        assertEquals(h1a, h1b);
        assertNotEquals(h1a, h1c);
        assertEquals(h1a.hashCode(), h1b.hashCode());
    }
}
