package ru.nsu.krasnyanskii.snake.model.board;

import ru.nsu.krasnyanskii.snake.model.entity.Point;

/**
 * Toroidal boundary rule: the snake exits one edge and re-enters from the opposite side.
 *
 * <p>{@link #apply(Point)} never returns {@code null} — every position is valid
 * after wrapping, so hitting an edge is never fatal with this strategy.</p>
 */
public final class WrapAroundBounds implements BoardBounds {

    private final int width;
    private final int height;

    public WrapAroundBounds(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    /**
     * Wraps {@code point} using modular arithmetic so it always lands inside the board.
     *
     * @return a new {@link Point} with coordinates clamped to [0, width) × [0, height).
     */
    @Override
    public Point apply(Point point) {
        int x = Math.floorMod(point.x(), width);
        int y = Math.floorMod(point.y(), height);
        return new Point(x, y);
    }
}
