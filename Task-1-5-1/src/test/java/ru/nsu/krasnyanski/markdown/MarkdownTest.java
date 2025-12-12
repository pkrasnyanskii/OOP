package ru.nsu.krasnyanski.markdown;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.markdown.text.Text;
import ru.nsu.krasnyanski.markdown.text.Bold;
import ru.nsu.krasnyanski.markdown.text.Italic;
import ru.nsu.krasnyanski.markdown.text.Strike;
import ru.nsu.krasnyanski.markdown.text.InlineCode;
import ru.nsu.krasnyanski.markdown.structure.Heading;
import ru.nsu.krasnyanski.markdown.structure.Quote;
import ru.nsu.krasnyanski.markdown.structure.Link;
import ru.nsu.krasnyanski.markdown.structure.Image;
import ru.nsu.krasnyanski.markdown.structure.Task;
import ru.nsu.krasnyanski.markdown.structure.CodeBlock;
import ru.nsu.krasnyanski.markdown.structure.ListElement;
import ru.nsu.krasnyanski.markdown.structure.Table;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class MarkdownTest {

    @Test
    void testTextStyles() {
        Text plain = new Text("hello");
        Bold bold = new Bold("bold");
        Italic italic = new Italic("italic");
        Strike strike = new Strike("strike");
        InlineCode code = new InlineCode("code");

        assertEquals("hello", plain.toMarkdown());
        assertEquals("**bold**", bold.toMarkdown());
        assertEquals("*italic*", italic.toMarkdown());
        assertEquals("~~strike~~", strike.toMarkdown());
        assertEquals("`code`", code.toMarkdown());
    }

    @Test
    void testHeading() {
        Heading h1 = new Heading(1, "Title");
        Heading h3 = new Heading(3, "Subtitle");

        assertEquals("# Title", h1.toMarkdown());
        assertEquals("### Subtitle", h3.toMarkdown());
    }

    @Test
    void testQuote() {
        Quote quote = new Quote(java.util.List.of(new Text("quoted text")));
        assertEquals("> quoted text", quote.toMarkdown());
    }

    @Test
    void testQuoteMultiLine() {
        Quote quote = new Quote(java.util.List.of(new Text("line1"), new Text("line2")));
        assertEquals("> line1\n> line2", quote.toMarkdown());
    }

    @Test
    void testLinkAndImage() {
        Link link = new Link("Google", "https://google.com");
        Image img = new Image("Alt", "image.png");

        assertEquals("[Google](https://google.com)", link.toMarkdown());
        assertEquals("![Alt](image.png)", img.toMarkdown());
    }

    @Test
    void testTask() {
        Task t1 = new Task("Do homework", false);
        Task t2 = new Task("Done task", true);

        assertEquals("- [ ] Do homework", t1.toMarkdown());
        assertEquals("- [x] Done task", t2.toMarkdown());
    }

    @Test
    void testCodeBlock() {
        CodeBlock cb = new CodeBlock("System.out.println(\"hi\");", "java");
        String expected = "```java\nSystem.out.println(\"hi\");\n```";
        assertEquals(expected, cb.toMarkdown());
    }

    @Test
    void testCodeBlockEmpty() {
        CodeBlock cb = new CodeBlock("", "");
        assertEquals("```\n\n```", cb.toMarkdown());
    }

    @Test
    void testListElement() {
        ListElement ul = new ListElement(false);
        ul.addItem(new Text("One"));
        ul.addItem(new Text("Two"));
        assertEquals("- One\n- Two", ul.toMarkdown());

        ListElement ol = new ListElement(true);
        ol.addItem(new Text("First"));
        ol.addItem(new Text("Second"));
        assertEquals("1. First\n2. Second", ol.toMarkdown());
    }

    @Test
    void testEmptyList() {
        ListElement ul = new ListElement(false);
        assertEquals("", ul.toMarkdown());
    }


    @Test
    void testTable() {
        Table.Builder builder = new Table.Builder()
                .withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_LEFT)
                .withRowLimit(5)
                .addRow("Index", "Random")
                .addRow(1, new Bold("8"))
                .addRow(2, 2)
                .addRow(3, 3)
                .addRow(4, new Bold("6"))
                .addRow(5, 3);

        Table table = builder.build();

        String expected =
                "| Index | Random |\n" +
                        "| ---: | :--- |\n" +
                        "| 1 | **8** |\n" +
                        "| 2 | 2 |\n" +
                        "| 3 | 3 |\n" +
                        "| 4 | **6** |\n" +
                        "| 5 | 3 |\n";

        assertEquals(expected, table.toMarkdown());
    }


    @Test
    void testEqualsAndHashCode() {
        Bold b1 = new Bold("text");
        Bold b2 = new Bold("text");
        Bold b3 = new Bold("other");

        assertEquals(b1, b2);
        assertNotEquals(b1, b3);
        assertEquals(b1.hashCode(), b2.hashCode());
    }
}
