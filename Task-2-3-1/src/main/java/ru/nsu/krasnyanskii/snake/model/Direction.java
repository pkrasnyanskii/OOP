package ru.nsu.krasnyanskii.snake.model;

/**
 * Represents the four possible movement directions for the snake.
 * Provides a utility to check if two directions are opposite,
 * preventing the snake from reversing into itself.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * @return true if this direction is the direct opposite of {@code other}.
     */
    public boolean isOpposite(Direction other) {
        return switch (this) {
            case UP -> other == DOWN;
            case DOWN -> other == UP;
            case LEFT -> other == RIGHT;
            case RIGHT -> other == LEFT;
        };
    }
}
