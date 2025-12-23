package ru.nsu.krasnyanski.markdown.structure;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageTest {

    @Test
    void testToMarkdown() {
        Image img = new Image("Alt", "img.png");
        assertEquals("![Alt](img.png)", img.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        Image i1 = new Image("alt", "url");
        Image i2 = new Image("alt", "url");
        Image i3 = new Image("alt", "other");

        assertEquals(i1, i2);
        assertNotEquals(i1, i3);
        assertEquals(i1.hashCode(), i2.hashCode());
    }
}
