package ru.nsu.krasnyanski.markdown.structure;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class CodeBlockTest {

    @Test
    void testToMarkdown() {
        CodeBlock cb = new CodeBlock("System.out.println(\"hi\");", "java");
        assertEquals("```java\nSystem.out.println(\"hi\");\n```", cb.toMarkdown());
    }

    @Test
    void testEmptyBlock() {
        CodeBlock cb = new CodeBlock("", "");
        assertEquals("```\n\n```", cb.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        CodeBlock cb1 = new CodeBlock("print", "py");
        CodeBlock cb2 = new CodeBlock("print", "py");
        CodeBlock cb3 = new CodeBlock("print2", "py");

        assertEquals(cb1, cb2);
        assertNotEquals(cb1, cb3);
        assertEquals(cb1.hashCode(), cb2.hashCode());
    }
}
