package ru.nsu.krasnyanski.markdown.text;

/**
 * Represents an inline code snippet in Markdown.
 * <p>Markdown syntax: <code>`code`</code>
 */
public class InlineCode extends Text {

    /**
     * Creates a new inline code element.
     *
     * @param content code content
     */
    public InlineCode(String content) {
        super(content);
    }

    /**
     * Converts the content to Markdown by wrapping it in backticks.
     *
     * @return Markdown inline code string
     */
    @Override
    public String toMarkdown() {
        return "`" + content + "`";
    }
}
