package ru.nsu.krasnyanski.markdown.structure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanski.markdown.text.Bold;

class TableTest {

    @Test
    void tableFromResource() {
        Table table = new Table.Builder()
                .withAlignments(Table.ALIGN_RIGHT, Table.ALIGN_LEFT)
                .withRowLimit(5)
                .addRow("Index", "Random")
                .addRow(1, new Bold("8"))
                .addRow(2, 2)
                .addRow(3, 3)
                .addRow(4, new Bold("6"))
                .addRow(5, 3)
                .build();

        String expected = """
                | Index | Random |
                | ---: | :--- |
                | 1 | **8** |
                | 2 | 2 |
                | 3 | 3 |
                | 4 | **6** |
                | 5 | 3 |
                """;

        assertEquals(expected, table.toMarkdown());
    }
}
