package ru.nsu.krasnyanski.markdown.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class TaskTest {

    @Test
    void testToMarkdown() {
        Task t1 = new Task("Do homework", false);
        Task t2 = new Task("Done task", true);

        assertEquals("- [ ] Do homework", t1.toMarkdown());
        assertEquals("- [x] Done task", t2.toMarkdown());
    }

    @Test
    void testEqualsAndHashCode() {
        Task t1 = new Task("A", true);
        Task t2 = new Task("A", true);
        Task t3 = new Task("B", false);

        assertEquals(t1, t2);
        assertNotEquals(t1, t3);
        assertEquals(t1.hashCode(), t2.hashCode());
    }
}
