package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.Objects;

public class Link implements Element {
    private final String text;
    private final String url;

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
        if(this == o) return true;
        if(!(o instanceof Link)) return false;
        Link l = (Link) o;
        return Objects.equals(text, l.text) && Objects.equals(url, l.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, url);
    }
}
