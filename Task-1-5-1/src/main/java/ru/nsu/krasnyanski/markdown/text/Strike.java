package ru.nsu.krasnyanski.markdown.text;

/**
 * Represents a strike-through text element in Markdown.
 * <p>Markdown syntax: <code>~~text~~</code>
 */
public class Strike extends Text {

    /**
     * Creates a new strike-through text element.
     *
     * @param content inner text
     */
    public Strike(String content) {
        super(content);
    }

    /**
     * Converts the content to Markdown by wrapping it in <code>~~</code>.
     *
     * @return Markdown-formatted strike-through text
     */
    @Override
    public String toMarkdown() {
        return "~~" + content + "~~";
    }
}
