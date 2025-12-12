package ru.nsu.krasnyanski.markdown.text;

/**
 * Represents bold text in Markdown.
 * <p>
 * Markdown syntax: <code>**text**</code>
 * </p>
 */
public class Bold extends Text {

    /**
     * Creates a new bold text element.
     *
     * @param content inner text
     */
    public Bold(String content) {
        super(content);
    }

    /**
     * Converts the content to Markdown by wrapping it in <code>**</code>.
     *
     * @return Markdown-formatted bold text
     */
    @Override
    public String toMarkdown() {
        return "**" + content + "**";
    }
}
