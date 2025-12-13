package ru.nsu.krasnyanski.markdown.text;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.Objects;

public class Text implements Element {
    protected final String content;

    public Text(String content) {
        this.content = content;
    }

    @Override
    public String toMarkdown() {
        return content;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Text)) return false;
        Text t = (Text) o;
        return Objects.equals(content, t.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }
}

