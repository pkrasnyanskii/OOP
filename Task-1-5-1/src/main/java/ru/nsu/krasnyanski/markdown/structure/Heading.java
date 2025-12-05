package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.Objects;

public class Heading implements Element {
    private final int level;
    private final String text;

    public Heading(int level, String text) {
        if(level < 1 || level > 6) throw new IllegalArgumentException("Heading level 1-6");
        this.level = level;
        this.text = text;
    }

    @Override
    public String toMarkdown() {
        return "#".repeat(level) + " " + text;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Heading)) return false;
        Heading h = (Heading) o;
        return level == h.level && Objects.equals(text, h.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, text);
    }
}
