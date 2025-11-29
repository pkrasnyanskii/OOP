package ru.nsu.krasnyanski.markdown.text;

public class InlineCode extends Text {
    public InlineCode(String content) { super(content); }

    @Override
    public String toMarkdown() {
        return "`" + content + "`";
    }
}
