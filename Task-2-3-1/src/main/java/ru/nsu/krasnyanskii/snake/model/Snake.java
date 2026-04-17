package ru.nsu.krasnyanskii.snake.model;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

/**
 * Manages the snake body as an ordered sequence of grid cells.
 *
 * <p>The body is stored in an {@link ArrayDeque}: {@code peekFirst()} is the head,
 * {@code peekLast()} is the tail. A parallel {@link HashSet} mirrors the deque
 * so that self-collision lookups run in O(1).</p>
 *
 * <p>Movement algorithm:</p>
 *   Prepend the new head position ({@code addFirst}).
 *   If {@code pendingGrowth > 0}, decrement it and skip tail removal — the snake grows.
 *   Otherwise, remove the tail segment ({@code removeLast}) — the snake slides.
 *
 * <p>This class is completely free of JavaFX and has no knowledge of board boundaries.
 * Boundary logic is handled by {@link ru.nsu.krasnyanskii.snake.model.board.BoardBounds} before
 * {@code move()} is called.</p>
 */
public class Snake {

    private final Deque<Point> body    = new ArrayDeque<>();
    private final Set<Point>   bodySet = new HashSet<>();
    private int pendingGrowth = 0;

    /**
     * Creates a snake with a single segment at {@code startPosition}.
     *
     * @param startPosition the initial head cell
     */
    public Snake(Point startPosition) {
        body.addFirst(startPosition);
        bodySet.add(startPosition);
    }

    /**
     * Moves the snake one step in {@code direction}.
     *
     * <p>The caller is responsible for supplying an already-validated
     * head position via {@link Point#move(Direction)} and
     * {@link ru.nsu.krasnyanskii.snake.model.board.BoardBounds#apply(Point)}
     * before calling this method.</p>
     *
     * @param direction the direction to move
     * @return the tail {@link Point} that was removed, or {@code null} if the snake grew
     */
    public Point move(Direction direction) {
        Point newHead = body.peekFirst().move(direction);
        body.addFirst(newHead);
        bodySet.add(newHead);

        if (pendingGrowth > 0) {
            pendingGrowth--;
            return null;
        }

        Point removedTail = body.removeLast();
        bodySet.remove(removedTail);
        return removedTail;
    }

    /**
     * Schedules a change in snake length to be applied over subsequent {@link #move} calls.
     *
     * <p>Positive {@code delta} queues growth segments. Negative {@code delta} immediately
     * removes tail segments, clamped so the snake always retains at least one segment.</p>
     *
     * @param delta segments to add (positive) or remove (negative)
     */
    public void scheduleGrowth(int delta) {
        if (delta > 0) {
            pendingGrowth += delta;
        } else {
            int shrink = Math.min(-delta, body.size() - 1);
            for (int i = 0; i < shrink; i++) {
                Point removed = body.removeLast();
                bodySet.remove(removed);
            }
        }
    }

    /**
     * Checks whether the given point overlaps any segment of this snake.
     *
     * @param point any grid cell
     * @return {@code true} if {@code point} overlaps any segment of this snake
     */
    public boolean contains(Point point) {
        return bodySet.contains(point);
    }

    /**
     * Detects self-collision by checking whether the current head appears more than
     * once in the body (which happens when the snake loops back into itself after a move).
     *
     * @return {@code true} if the head overlaps any body segment behind it
     */
    public boolean selfCollision() {
        Point head = getHead();
        return body.stream().filter(p -> p.equals(head)).count() > 1;
    }

    /**
     * Returns the head cell (front of the body).
     *
     * @return the head cell (front of the body)
     */
    public Point getHead() {
        return body.peekFirst();
    }

    /**
     * Returns the tail cell (back of the body).
     *
     * @return the tail cell (back of the body)
     */
    public Point getTail() {
        return body.peekLast();
    }

    /**
     * Returns the current number of segments.
     *
     * @return the current number of segments
     */
    public int getLength() {
        return body.size();
    }

    /**
     * Returns an unmodifiable snapshot of the body in head-first order.
     * Consumed by the view layer for rendering only.
     *
     * @return unmodifiable list of body cells, head at index 0
     */
    public List<Point> getBody() {
        return Collections.unmodifiableList(body.stream().toList());
    }

    /** Replaces the head cell without changing the body length. Used for wrap-around. */
    public void teleportHead(Point newHead) {
        body.addFirst(newHead);
        bodySet.add(newHead);

        if (pendingGrowth > 0) {
            pendingGrowth--;
            return;
        }

        Point removedTail = body.removeLast();
        bodySet.remove(removedTail);
    }
}
