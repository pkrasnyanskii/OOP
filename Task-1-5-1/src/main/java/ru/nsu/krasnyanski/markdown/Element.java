package ru.nsu.krasnyanski.markdown;

/**
 * Base interface for all Markdown elements.
 * Every element must support serialization into Markdown format.
 */
public interface Element {

    /**
     * Serializes element into Markdown string.
     *
     * @return Markdown representation of the element
     */
    String toMarkdown();
}