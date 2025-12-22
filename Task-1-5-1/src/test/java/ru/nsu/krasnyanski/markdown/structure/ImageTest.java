package ru.nsu.krasnyanski.markdown.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URL;
import org.junit.jupiter.api.Test;

class ImageTest {

    @Test
    void imageFromResource() {
        URL resource = getClass()
                .getClassLoader()
                .getResource("markdown/test-image.jpg");

        assertNotNull(resource, "Image resource not found");

        String path = resource.toString();

        Image img = new Image("Test image", path);

        String expected = """
            ![Test image](%s)
            """.formatted(path).stripIndent().stripTrailing();

        assertEquals(expected, img.toMarkdown());
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
