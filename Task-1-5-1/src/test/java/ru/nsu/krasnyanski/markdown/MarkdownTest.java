package ru.nsu.krasnyanski.markdown;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ru.nsu.krasnyanski.markdown.structure.CodeBlock;
import ru.nsu.krasnyanski.markdown.structure.Heading;
import ru.nsu.krasnyanski.markdown.structure.Image;
import ru.nsu.krasnyanski.markdown.structure.Link;
import ru.nsu.krasnyanski.markdown.structure.ListElement;
import ru.nsu.krasnyanski.markdown.structure.MarkdownException;
import ru.nsu.krasnyanski.markdown.structure.Quote;
import ru.nsu.krasnyanski.markdown.structure.Table;
import ru.nsu.krasnyanski.markdown.structure.Task;
import ru.nsu.krasnyanski.markdown.text.Bold;
import ru.nsu.krasnyanski.markdown.text.InlineCode;
import ru.nsu.krasnyanski.markdown.text.Italic;
import ru.nsu.krasnyanski.markdown.text.Strike;
import ru.nsu.krasnyanski.markdown.text.Text;

class MarkdownTest {

    @Test
    void testTextStyles() {
        Text plain = new Text("hello");
        Bold bold = new Bold("bold");
        Italic italic = new Italic("italic");
        final Strike strike = new Strike("strike");
        final InlineCode code = new InlineCode("code");

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
    void testHeadingEdgeCases() {
        assertThrows(MarkdownException.class, () -> new Heading(0, "Too low"));
        assertThrows(MarkdownException.class, () -> new Heading(7, "Too high"));

        Heading h1a = new Heading(1, "A");
        Heading h1b = new Heading(1, "A");
        Heading h1c = new Heading(1, "B");

        assertEquals(h1a, h1b);
        assertNotEquals(h1a, h1c);
        assertEquals(h1a.hashCode(), h1b.hashCode());
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
    void testQuoteEdgeCases() {
        Quote emptyQuote = new Quote(java.util.List.of());
        assertEquals("", emptyQuote.toMarkdown());

        Quote multi = new Quote(java.util.List.of(new Text("one"), new Text("two")));
        assertEquals("> one\n> two", multi.toMarkdown());
    }

    @Test
    void testQuoteAdvanced() {
        Quote q1 = new Quote(java.util.List.of(new Text("")));
        assertEquals("> ", q1.toMarkdown());

        Quote q2 = new Quote(java.util.List.of(new Text("line1"), new Text("   "), new Text("line2")));
        assertEquals("> line1\n>    \n> line2", q2.toMarkdown());

        Quote q3 = new Quote(java.util.List.of(
                new Bold("hello"),
                new Italic("world")
        ));
        assertEquals("> **hello**\n> *world*", q3.toMarkdown());

        Quote qA = new Quote(java.util.List.of(new Text("x")));
        Quote qB = new Quote(java.util.List.of(new Text("x")));
        Quote qC = new Quote(java.util.List.of(new Text("y")));

        assertEquals(qA, qB);
        assertNotEquals(qA, qC);
        assertEquals(qA.hashCode(), qB.hashCode());
    }


    @Test
    void testLinkAndImage() {
        Link link = new Link("Google", "https://google.com");
        Image img = new Image("Alt", "image.png");

        assertEquals("[Google](https://google.com)", link.toMarkdown());
        assertEquals("![Alt](image.png)", img.toMarkdown());
    }

    @Test
    void testImageAndLinkEdgeCases() {
        Image i1 = new Image("alt", "url");
        Image i2 = new Image("alt", "url");
        Image i3 = new Image("alt", "other");
        assertEquals(i1, i2);
        assertNotEquals(i1, i3);

        Link l1 = new Link("name", "href");
        Link l2 = new Link("name", "href");
        Link l3 = new Link("name", "other");
        assertEquals(l1, l2);
        assertNotEquals(l1, l3);
    }

    @Test
    void testTask() {
        Task t1 = new Task("Do homework", false);
        Task t2 = new Task("Done task", true);

        assertEquals("- [ ] Do homework", t1.toMarkdown());
        assertEquals("- [x] Done task", t2.toMarkdown());
    }

    @Test
    void testTaskEdgeCases() {
        Task empty = new Task("", false);
        assertEquals("- [ ] ", empty.toMarkdown());

        Task spaces = new Task("   ", true);
        assertEquals("- [x]    ", spaces.toMarkdown());

        Task a1 = new Task("A", false);
        Task a2 = new Task("A", false);
        Task a3 = new Task("A", true);
        Task b = new Task("B", false);

        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertNotEquals(a1, b);
        assertEquals(a1.hashCode(), a2.hashCode());
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
    void testCodeBlockEdgeCases() {
        CodeBlock cbLang = new CodeBlock("println", "kotlin");
        assertEquals("```kotlin\nprintln\n```", cbLang.toMarkdown());

        CodeBlock cb2 = new CodeBlock("println", "kotlin");
        assertEquals(cbLang, cb2);
        assertEquals(cbLang.hashCode(), cb2.hashCode());
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
    void testListElementEdgeCases() {
        ListElement emptyUl = new ListElement(false);
        ListElement emptyOl = new ListElement(true);
        assertEquals("", emptyUl.toMarkdown());
        assertEquals("", emptyOl.toMarkdown());

        ListElement ul = new ListElement(false);
        ul.addItem(new Text("Only"));
        assertEquals("- Only", ul.toMarkdown());

        ListElement ol = new ListElement(true);
        ol.addItem(new Text("First"));
        assertEquals("1. First", ol.toMarkdown());
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
                "| Index | Random |\n"
                        + "| ---: | :--- |\n"
                        + "| 1 | **8** |\n"
                        + "| 2 | 2 |\n"
                        + "| 3 | 3 |\n"
                        + "| 4 | **6** |\n"
                        + "| 5 | 3 |\n";

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
