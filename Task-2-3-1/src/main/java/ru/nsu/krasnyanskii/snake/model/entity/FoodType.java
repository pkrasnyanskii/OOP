package ru.nsu.krasnyanskii.snake.model.entity;

/**
 * Classifies food items by their effect on the snake and score.
 *   {@link #NORMAL}  — grows snake by 1, awards 1 point.
 *   {@link #BONUS}   — grows snake by 1, awards 3 points (golden, rare).
 *   {@link #SHRINK}  — shrinks snake by 1, awards 5 points (high-risk reward).
 */
public enum FoodType {
    NORMAL(1,  1),
    BONUS (3,  1),
    SHRINK(5, -1);

    private final int scoreValue;
    private final int growthDelta;

    FoodType(int scoreValue, int growthDelta) {
        this.scoreValue  = scoreValue;
        this.growthDelta = growthDelta;
    }

    /** Points awarded when this food item is consumed. */
    public int getScoreValue()  { return scoreValue; }

    /** Change in snake length when this food item is consumed (positive = grow, negative = shrink). */
    public int getGrowthDelta() { return growthDelta; }
}
