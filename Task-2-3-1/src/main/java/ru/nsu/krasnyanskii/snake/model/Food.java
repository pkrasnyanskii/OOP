package ru.nsu.krasnyanskii.snake.model;

/**
 * Represents a single food item on the board.
 * Immutable — a new Food object is created whenever food appears on the grid,
 * which avoids accidental mutation and makes equality checks straightforward.
 */
public final class Food {
    private final Point position;
    private final FoodType type;

    public Food(Point position, FoodType type) {
        this.position = position;
        this.type = type;
    }

    public Point getPosition() { return position; }
    public FoodType getType()   { return type; }
}
