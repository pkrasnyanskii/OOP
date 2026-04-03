package ru.nsu.krasnyanskii.snake.model.board;

import ru.nsu.krasnyanskii.snake.model.entity.Point;

/**
 * Strategy for what happens when the snake reaches a board edge.
 *
 * <p>Two built-in implementations are provided:
 * {@link SolidWallBounds} (classic: hitting a wall kills the snake) and
 * {@link WrapAroundBounds} (toroidal: the snake exits one side and enters the opposite).</p>
 *
 * <p>Adding a third boundary rule (e.g. bouncing) requires only a new implementation
 * of this interface — no changes to {@link ru.nsu.krasnyanskii.snake.model.GameModel} are needed
 * (open/closed principle).</p>
 */
public interface BoardBounds {

    /**
     * Wraps or validates {@code point} according to this boundary rule.
     *
     * @param point the candidate position after one movement step
     * @return the effective position the snake should occupy, or {@code null}
     *         if the move is fatal (i.e. the snake hits a solid wall)
     */
    Point apply(Point point);
}
