package ru.nsu.krasnyanskii.snake.model.board;

import ru.nsu.krasnyanskii.snake.model.entity.Point;

/**
 * Classic boundary rule: moving outside the board is fatal.
 *
 * <p>{@link #apply(Point)} returns {@code null} for any out-of-bounds position,
 * which {@link ru.nsu.krasnyanskii.snake.model.GameModel} interprets as a game-over collision.</p>
 */
public final class SolidWallBounds implements BoardBounds {

    private final int width;
    private final int height;

    public SolidWallBounds(int width, int height) {
        this.width  = width;
        this.height = height;
    }

    /**
     * @return {@code null} when {@code point} is outside the board; {@code point} otherwise.
     */
    @Override
    public Point apply(Point point) {
        if (point.x() < 0 || point.x() >= width
                || point.y() < 0 || point.y() >= height) {
            return null;
        }
        return point;
    }
}
