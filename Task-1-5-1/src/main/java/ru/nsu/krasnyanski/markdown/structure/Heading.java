package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.Objects;

/**
 * Represents a Markdown heading (levels 1–6).
 * <p>
 * Markdown syntax: <code>## Heading</code>
 * </p>
 */
public class Heading implements Element {
    private final int level;
    private final String text;

    /**
     * Creates a heading.
     *
     * @param level heading level (1–6)
     * @param text heading content
     * @throws IllegalArgumentException if level is outside 1–6
     */
    public Heading(int level, String text) {
        if (level < 1 || level > 6){
            throw new IllegalArgumentException("Heading level 1-6");
        }
        this.level = level;
        this.text = text;
    }

    @Override
    public String toMarkdown() {
        return "#".repeat(level) + " " + text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof Heading)){
            return false;
        }
        Heading h = (Heading) o;
        return level == h.level && Objects.equals(text, h.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, text);
    }
}
