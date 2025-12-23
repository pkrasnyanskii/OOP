package ru.nsu.krasnyanski.markdown.structure;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.markdown.text.Bold;
import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    @Test
    void testToMarkdown() {
        Table.Builder builder = new Table.Builder()
                .withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_LEFT)
                .withRowLimit(5)
                .addRow("Index", "Random")
                .addRow(1, new Bold("8"))
                .addRow(2, 2);

        Table table = builder.build();

        String expected =
                "| Index | Random |\n" +
                        "| ---: | :--- |\n" +
                        "| 1 | **8** |\n" +
                        "| 2 | 2 |\n";

        assertEquals(expected, table.toMarkdown());
    }
}
