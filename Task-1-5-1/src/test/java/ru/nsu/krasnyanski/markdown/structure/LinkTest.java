package ru.nsu.krasnyanski.markdown.structure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LinkTest {

    @Test
    void testToMarkdown() {
        Link link = new Link("Google", "https://google.com");
        assertEquals("[Google](https://google.com)", link.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        Link l1 = new Link("name", "href");
        Link l2 = new Link("name", "href");
        Link l3 = new Link("name", "other");

        assertEquals(l1, l2);
        assertNotEquals(l1, l3);
        assertEquals(l1.hashCode(), l2.hashCode());
    }
}
