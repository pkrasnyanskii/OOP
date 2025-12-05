package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class ListElement implements Element {
    private final List<Element> items;
    private final boolean ordered;

    public ListElement(boolean ordered) {
        this.ordered = ordered;
        this.items = new ArrayList<>();
    }

    public void addItem(Element item) {
        items.add(item);
    }

    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for(Element e : items) {
            if(ordered) {
                sb.append(index++).append(". ").append(e.toMarkdown()).append("\n");
            } else {
                sb.append("- ").append(e.toMarkdown()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof ListElement)) return false;
        ListElement l = (ListElement) o;
        return ordered == l.ordered && Objects.equals(items, l.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, ordered);
    }
}
