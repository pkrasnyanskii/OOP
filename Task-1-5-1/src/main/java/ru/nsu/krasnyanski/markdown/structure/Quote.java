package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Markdown blockquote.
 * <p>
 * Each element forms a new line prefixed with <code>> </code>.
 * </p>
 */
public class Quote implements Element {
    private final List<Element> elements;

    /**
     * Creates a multiline Markdown quote.
     *
     * @param elements list of elements inside quote
     */
    public Quote(List<Element> elements) {
        this.elements = elements;
    }

    /**
     * Converts the content into Markdown quote format.
     */
    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for (Element e : elements) {
            sb.append("> ").append(e.toMarkdown()).append("\n");
        }
        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quote)) return false;
        Quote q = (Quote) o;
        return Objects.equals(elements, q.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
