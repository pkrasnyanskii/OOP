package ru.nsu.krasnyanski.markdown.text;

/**
 * Represents italic text in Markdown.
 *
 * <p>Markdown syntax: <code>*text*</code>
 */
public class Italic extends Text {

    /**
     * Creates a new italic text element.
     *
     * @param content inner text
     */
    public Italic(String content) {
        super(content);
    }

    /**
     * Converts the content to Markdown by wrapping it in <code>*</code>.
     *
     * @return Markdown-formatted italic text
     */
    @Override
    public String toMarkdown() {
        return "*" + content + "*";
    }
}
