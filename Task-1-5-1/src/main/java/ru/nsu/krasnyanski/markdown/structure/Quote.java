package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.List;
import java.util.Objects;

public class Quote implements Element {
    private final List<Element> elements;

    public Quote(List<Element> elements) {
        this.elements = elements;
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        for(Element e: elements) {
            sb.append("> ").append(e.toMarkdown()).append("\n");
        }
        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Quote)) return false;
        Quote q = (Quote) o;
        return Objects.equals(elements, q.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(elements);
    }
}
