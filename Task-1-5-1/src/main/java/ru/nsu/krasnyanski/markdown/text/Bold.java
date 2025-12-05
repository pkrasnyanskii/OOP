package ru.nsu.krasnyanski.markdown.text;

public class Bold extends Text {
    public Bold(String content) { super(content); }

    @Override
    public String toMarkdown() {
        return "**" + content + "**";
    }
}
