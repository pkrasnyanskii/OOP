package ru.nsu.krasnyanski.markdown.structure;

import java.util.Objects;
import ru.nsu.krasnyanski.markdown.Element;

/**
 * Represents a Markdown image.
 * <p>
 * Markdown syntax: <code>![alt](url)</code>
 * </p>
 */
public class Image implements Element {
    private final String alt;
    private final String url;

    /**
     * Creates an image element.
     *
     * @param alt alternative text
     * @param url image URL
     */
    public Image(String alt, String url) {
        this.alt = alt;
        this.url = url;
    }

    @Override
    public String toMarkdown() {
        return "![" + alt + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Image)) {
            return false;
        }
        Image img = (Image) o;
        return Objects.equals(alt, img.alt) && Objects.equals(url, img.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alt, url);
    }
}
