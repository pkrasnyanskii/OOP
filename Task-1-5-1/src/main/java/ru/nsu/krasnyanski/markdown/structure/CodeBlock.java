package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.Objects;

public class CodeBlock implements Element {
    private final String code;
    private final String language;

    public CodeBlock(String code, String language) {
        this.code = code;
        this.language = language;
    }

    @Override
    public String toMarkdown() {
        return "```" + language + "\n" + code + "\n```";
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof CodeBlock)) return false;
        CodeBlock c = (CodeBlock) o;
        return Objects.equals(code, c.code) && Objects.equals(language, c.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, language);
    }
}
