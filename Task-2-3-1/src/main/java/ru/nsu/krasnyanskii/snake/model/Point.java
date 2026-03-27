package ru.nsu.krasnyanskii.snake.model;

/**
 * Immutable value object representing a cell position on the game grid.
 * Uses Java records to guarantee immutability and provides equals/hashCode
 * automatically — important for Set membership checks (e.g. collision detection).
 */
public record Point(int x, int y) {

    /**
     * Returns a new Point shifted by one cell in the given direction.
     */
    public Point move(Direction direction) {
        return switch (direction) {
            case UP    -> new Point(x, y - 1);
            case DOWN  -> new Point(x, y + 1);
            case LEFT  -> new Point(x - 1, y);
            case RIGHT -> new Point(x + 1, y);
        };
    }
}
