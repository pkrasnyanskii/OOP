package ru.nsu.krasnyanskii.snake.model;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Pure-model class for the snake.
 * <p>
 * The snake body is stored as an {@link ArrayDeque} of {@link Point}s:
 * the front (head) is {@code body.peekFirst()}, the tail is {@code body.peekLast()}.
 * A parallel {@link HashSet} is kept for O(1) self-collision lookup.
 * </p>
 * <p>
 * Movement algorithm (per task spec §3):
 * <ol>
 *   <li>Add a new head in the current direction (addFirst).</li>
 *   <li>If the snake should grow (pendingGrowth > 0), skip tail removal.</li>
 *   <li>Otherwise remove the tail segment (removeLast).</li>
 * </ol>
 * This class has NO JavaFX dependency — it is a plain Java model.
 * </p>
 */
public class Snake {

    private final Deque<Point> body = new ArrayDeque<>();
    /** Mirror set for O(1) collision check. */
    private final Set<Point> bodySet = new HashSet<>();
    /** Segments of growth still to be applied. */
    private int pendingGrowth = 0;

    public Snake(Point startPosition) {
        body.addFirst(startPosition);
        bodySet.add(startPosition);
    }

    // ------------------------------------------------------------------ //
    //  Movement                                                            //
    // ------------------------------------------------------------------ //

    /**
     * Moves the snake one step in {@code direction}.
     *
     * @return the tail segment that was removed (or {@code null} if the snake grew).
     */
    public Point move(Direction direction) {
        Point newHead = body.peekFirst().move(direction);
        body.addFirst(newHead);
        bodySet.add(newHead);

        if (pendingGrowth > 0) {
            pendingGrowth--;
            return null;           // no tail removed — snake grew
        }

        Point removedTail = body.removeLast();
        bodySet.remove(removedTail);
        return removedTail;
    }

    // ------------------------------------------------------------------ //
    //  Growth                                                              //
    // ------------------------------------------------------------------ //

    /**
     * Schedules growth (or shrinkage when delta is negative).
     * Shrinking below 1 segment is clamped — the snake cannot disappear.
     *
     * @param delta positive to grow, negative to shrink.
     */
    public void scheduleGrowth(int delta) {
        if (delta > 0) {
            pendingGrowth += delta;
        } else {
            // Shrink: remove segments from the tail immediately.
            int shrink = Math.min(-delta, body.size() - 1); // keep at least 1
            for (int i = 0; i < shrink; i++) {
                Point removed = body.removeLast();
                bodySet.remove(removed);
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Collision queries                                                   //
    // ------------------------------------------------------------------ //

    /**
     * @return true if {@code point} overlaps any segment of the snake body.
     */
    public boolean contains(Point point) {
        return bodySet.contains(point);
    }

    /**
     * Checks whether the given point would collide with the snake body,
     * EXCLUDING the current head (used for self-collision after move).
     * After move(), the new head has already been added, so we check
     * if the bodySet contains the point more than once — i.e. the rest of
     * the body contains it.
     */
    public boolean selfCollision() {
        Point head = getHead();
        // Count occurrences: if head appears in body more than once → collision
        return body.stream().filter(p -> p.equals(head)).count() > 1;
    }

    // ------------------------------------------------------------------ //
    //  Accessors                                                           //
    // ------------------------------------------------------------------ //

    public Point getHead() { return body.peekFirst(); }
    public Point getTail() { return body.peekLast(); }
    public int   getLength() { return body.size(); }

    /**
     * Returns an unmodifiable view of the body (head-first order).
     * Used by the view layer for rendering.
     */
    public List<Point> getBody() {
        return Collections.unmodifiableList(body.stream().toList());
    }
}
