package ru.nsu.krasnyanskii.snake.model.entity;

/**
 * Immutable value object representing a single cell position on the game grid.
 *
 * <p>Implemented as a Java {@code record}, which auto-generates {@code equals},
 * {@code hashCode}, and {@code toString} — important for O(1) {@link java.util.HashSet}
 * membership checks used in collision detection.</p>
 *
 * @param x column index (0-based, left to right)
 * @param y row index (0-based, top to bottom)
 */
public record Point(int x, int y) {

    /**
     * Returns a new {@code Point} shifted by one cell in {@code direction}.
     * The result may be outside the board — boundary handling is delegated to
     * {@link ru.nsu.krasnyanskii.snake.model.board.BoardBounds}.
     *
     * @param direction the direction to move
     * @return a new {@code Point} one step away
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
