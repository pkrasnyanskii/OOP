package ru.nsu.krasnyanskii.snake.model.entity;

/**
 * Immutable representation of a single food item on the board.
 *
 * <p>A new instance is created every time food appears on the grid,
 * preventing accidental mutation of in-flight food state.</p>
 */
public final class Food {

    private final Point    position;
    private final FoodType type;

    public Food(Point position, FoodType type) {
        this.position = position;
        this.type     = type;
    }

    /** @return the grid cell this food occupies */
    public Point    getPosition() { return position; }

    /** @return the type that determines score and growth effect */
    public FoodType getType()     { return type; }
}
