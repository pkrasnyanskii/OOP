package ru.nsu.krasnyanski.markdown.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.markdown.text.Text;


class QuoteTest {

    @Test
    void testToMarkdownSingleLine() {
        Quote q = new Quote(List.of(new Text("quoted")));
        assertEquals("> quoted", q.toMarkdown());
    }

    @Test
    void testToMarkdownMultiLine() {
        Quote q = new Quote(List.of(new Text("line1"), new Text("line2")));
        assertEquals("> line1\n> line2", q.toMarkdown());
    }

    @Test
    void testEmptyQuote() {
        Quote q = new Quote(List.of());
        assertEquals("", q.toMarkdown());
    }
}
