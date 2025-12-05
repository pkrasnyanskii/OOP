package ru.nsu.krasnyanski.markdown.text;

public class Strike extends Text {
    public Strike(String content) { super(content); }

    @Override
    public String toMarkdown() {
        return "~~" + content + "~~";
    }
}
