package ru.nsu.krasnyanski.markdown.structure;

import ru.nsu.krasnyanski.markdown.Element;
import java.util.Objects;

public class Task implements Element {
    private final String description;
    private final boolean done;

    public Task(String description, boolean done) {
        this.description = description;
        this.done = done;
    }

    @Override
    public String toMarkdown() {
        return (done ? "- [x] " : "- [ ] ") + description;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Task)) return false;
        Task t = (Task) o;
        return done == t.done && Objects.equals(description, t.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, done);
    }
}
