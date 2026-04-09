package ru.nsu.krasnyanskii.snake.model.entity;

/**
 * Classifies food items by their effect on the snake and score.
 *   {@link #NORMAL}  — grows snake by 1, awards 1 point.
 *   {@link #BONUS}   — grows snake by 1, awards 3 points (golden, rare).
 *   {@link #SHRINK}  — shrinks snake by 1, awards 5 points (high-risk reward).
 */
public enum FoodType {
    NORMAL(1, 1),
    BONUS(3, 1),
    SHRINK(5, -1);

    private final int scoreValue;
    private final int growthDelta;

    FoodType(int scoreValue, int growthDelta) {
        this.scoreValue  = scoreValue;
        this.growthDelta = growthDelta;
    }

    /**
     * Returns the points awarded when this food item is consumed.
     *
     * @return points awarded when this food item is consumed
     */
    public int getScoreValue() {
        return scoreValue;
    }

    /**
     * Returns the change in snake length when this food item is consumed.
     *
     * @return segments to add (positive) or remove (negative)
     */
    public int getGrowthDelta() {
        return growthDelta;
    }
}
