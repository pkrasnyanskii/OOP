package ru.nsu.krasnyanski.markdown.text;

public class Italic extends Text {
    public Italic(String content) { super(content); }

    @Override
    public String toMarkdown() {
        return "*" + content + "*";
    }
}
