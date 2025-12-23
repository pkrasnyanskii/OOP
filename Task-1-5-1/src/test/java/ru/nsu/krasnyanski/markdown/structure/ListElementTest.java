package ru.nsu.krasnyanski.markdown.structure;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.markdown.text.Text;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ListElementTest {

    @Test
    void testUnorderedList() {
        ListElement ul = new ListElement(false);
        ul.addItem(new Text("One"));
        ul.addItem(new Text("Two"));
        assertEquals("- One\n- Two", ul.toMarkdown());
    }

    @Test
    void testOrderedList() {
        ListElement ol = new ListElement(true);
        ol.addItem(new Text("First"));
        ol.addItem(new Text("Second"));
        assertEquals("1. First\n2. Second", ol.toMarkdown());
    }

    @Test
    void testEmptyList() {
        assertEquals("", new ListElement(false).toMarkdown());
        assertEquals("", new ListElement(true).toMarkdown());
    }
}
