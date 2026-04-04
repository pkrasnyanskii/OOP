package ru.nsu.krasnyanskii.snake.model.entity;

/**
 * The four cardinal movement directions for the snake.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * @param other another direction
     * @return {@code true} if {@code other} is the direct opposite of this direction
     */
    public boolean isOpposite(Direction other) {
        return switch (this) {
            case UP    -> other == DOWN;
            case DOWN  -> other == UP;
            case LEFT  -> other == RIGHT;
            case RIGHT -> other == LEFT;
        };
    }
}
