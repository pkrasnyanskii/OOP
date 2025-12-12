package ru.nsu.krasnyanski.markdown.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.nsu.krasnyanski.markdown.Element;

/**
 * Represents an ordered or unordered Markdown list.
 */
public class ListElement implements Element {
    private final List<Element> items;
    private final boolean ordered;

    /**
     * Creates a new list.
     *
     * @param ordered true for numbered list, false for bullet list
     */
    public ListElement(boolean ordered) {
        this.ordered = ordered;
        this.items = new ArrayList<>();
    }

    /**
     * Adds a list item.
     *
     * @param item element to add
     */
    public void addItem(Element item) {
        items.add(item);
    }

    /**
     * Converts list items to Markdown.
     * <ul>
     *     <li><code>- item</code> for unordered</li>
     *     <li><code>1. item</code> for ordered</li>
     * </ul>
     */
    @Override
    public String toMarkdown() {
        StringBuilder sb = new StringBuilder();
        int index = 1;
        for (Element e : items) {
            if (ordered) {
                sb.append(index++).append(". ").append(e.toMarkdown()).append("\n");
            } else {
                sb.append("- ").append(e.toMarkdown()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ListElement)) {
            return false;
        }
        ListElement l = (ListElement) o;
        return ordered == l.ordered && Objects.equals(items, l.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, ordered);
    }
}
