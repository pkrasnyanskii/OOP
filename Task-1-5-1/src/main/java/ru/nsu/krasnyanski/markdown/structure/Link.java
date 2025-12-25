package ru.nsu.krasnyanski.markdown.structure;

import java.util.Objects;
import ru.nsu.krasnyanski.markdown.Element;

/**
 * Represents a Markdown hyperlink.
 *
 * <p>Markdown syntax: <code>[text](url)</code>
 */
public class Link implements Element {
    private final String text;
    private final String url;

    /**
     * Creates a new Markdown link.
     *
     * @param text visible label
     * @param url destination URL
     */
    public Link(String text, String url) {
        this.text = text;
        this.url = url;
    }

    @Override
    public String toMarkdown() {
        return "[" + text + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Link)) {
            return false;
        }
        Link l = (Link) o;
        return Objects.equals(text, l.text) && Objects.equals(url, l.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, url);
    }
}
