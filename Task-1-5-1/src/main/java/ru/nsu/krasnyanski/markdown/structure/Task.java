package ru.nsu.krasnyanski.markdown.structure;

import java.util.Objects;
import ru.nsu.krasnyanski.markdown.Element;

/**
 * Represents a Markdown task list item.
 * <p>
 * Markdown syntax:
 * <ul>
 *     <li><code>- [ ] description</code> — unchecked task</li>
 *     <li><code>- [x] description</code> — completed task</li>
 * </ul>
 */
public class Task implements Element {
    private final String description;
    private final boolean done;

    /**
     * Creates a new task list item.
     *
     * @param description text of the task
     * @param done whether the task is completed
     */
    public Task(String description, boolean done) {
        this.description = description;
        this.done = done;
    }

    /**
     * Converts this task into Markdown string.
     *
     * @return formatted task list item
     */
    @Override
    public String toMarkdown() {
        return (done ? "- [x] " : "- [ ] ") + description;
    }

    /** Equality checks both description and completion flag. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Task)) {
            return false;
        }
        Task t = (Task) o;
        return done == t.done && Objects.equals(description, t.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, done);
    }
}
