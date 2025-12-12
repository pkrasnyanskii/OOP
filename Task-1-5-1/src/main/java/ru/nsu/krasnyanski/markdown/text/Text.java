package ru.nsu.krasnyanski.markdown.text;

import java.util.Objects;
import ru.nsu.krasnyanski.markdown.Element;

/**
 * Represents a plain text element in a Markdown document.
 * <p>This is the base class for all inline Markdown text elements such as bold, italic,
 * strike-through, and inline code. It stores raw textual content and returns it unchanged
 * in {@link #toMarkdown()}, unless overridden by subclasses.
 */
public class Text implements Element {
    protected final String content;

    /**
     * Creates a new plain text element.
     *
     * @param content the text content (not null)
     */
    public Text(String content) {
        this.content = content;
    }

    /**
     * Returns the content unchanged, because plain text
     * does not require any Markdown formatting.
     *
     * @return raw text content
     */
    @Override
    public String toMarkdown() {
        return content;
    }

    /**
     * Checks equality by comparing classes and content.
     *
     * @param o the object to compare
     * @return true if both are Text instances with the same content
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Text)) {
            return false;
        }
        Text t = (Text) o;
        return Objects.equals(content, t.content);
    }

    /**
     * Computes hash code based on content.
     *
     * @return hash code for this Text
     */
    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}
