package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.text.Text;
import ru.nsu.krasnyanski.markdown.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Table implements Element {
    public static final int ALIGN_LEFT = 0;
    public static final int ALIGN_CENTER = 1;
    public static final int ALIGN_RIGHT = 2;

    private final List<List<Element>> rows;
    private final int[] alignments;
    private final int rowLimit;

    private Table(List<List<Element>> rows, int[] alignments, int rowLimit) {
        this.rows = rows;
        this.alignments = alignments;
        this.rowLimit = rowLimit;
    }

    @Override
    public String toMarkdown() {
        if (rows.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        List<Element> header = rows.get(0);

        sb.append("|");
        for (Element e : header) sb.append(" ").append(e.toMarkdown()).append(" |");
        sb.append("\n");

        sb.append("|");
        for (int i = 0; i < header.size(); i++) {
            int align = (i < alignments.length) ? alignments[i] : ALIGN_LEFT;
            switch (align) {
                case ALIGN_LEFT -> sb.append(" :--- |");
                case ALIGN_CENTER -> sb.append(" :---: |");
                case ALIGN_RIGHT -> sb.append(" ---: |");
                default -> sb.append(" --- |");
            }
        }
        sb.append("\n");

        for (int i = 1; i < Math.min(rows.size(), rowLimit + 1); i++) {
            sb.append("|");
            for (Element e : rows.get(i)) {
                sb.append(" ").append(e.toMarkdown()).append(" |");
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Table)) return false;
        Table t = (Table) o;
        return Objects.equals(rows, t.rows) &&
                rowLimit == t.rowLimit &&
                Objects.equals(alignments, t.alignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rows, rowLimit, alignments);
    }

    public static class Builder {
        private final List<List<Element>> rows = new ArrayList<>();
        private int[] alignments = new int[0];
        private int rowLimit = Integer.MAX_VALUE;

        public Builder withAlignments(int... aligns) {
            this.alignments = aligns;
            return this;
        }

        public Builder withRowLimit(int limit) {
            this.rowLimit = limit;
            return this;
        }

        public Builder addRow(Object... cells) {
            List<Element> row = new ArrayList<>();
            for (Object c : cells) {
                if (c instanceof Element e) row.add(e);
                else row.add(new Text(c.toString()));
            }
            rows.add(row);
            return this;
        }

        public Table build() {
            return new Table(rows, alignments, rowLimit);
        }
    }
}
