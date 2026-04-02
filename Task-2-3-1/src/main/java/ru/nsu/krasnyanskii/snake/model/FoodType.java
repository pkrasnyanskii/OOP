package ru.nsu.krasnyanskii.snake.model;

/**
 * Enumerates available food types.
 * Adding a new food type here (and handling it in {@link Snake#eatFood})
 * is the only change required to introduce new food behaviour — open/closed principle.
 *
 * NORMAL  – grows snake by 1, scores 1 point.
 * BONUS   – grows snake by 1, scores 3 points (visually distinct golden food).
 * SHRINK  – shrinks snake by 1 (dangerous food), scores 5 points as a risk reward.
 */
public enum FoodType {
    NORMAL(1, 1),
    BONUS(3, 1),
    SHRINK(5, -1);

    /** Points awarded when this food is consumed. */
    private final int scoreValue;
    /** Growth delta applied to the snake (+1 normal, -1 shrink). */
    private final int growthDelta;

    FoodType(int scoreValue, int growthDelta) {
        this.scoreValue = scoreValue;
        this.growthDelta = growthDelta;
    }

    public int getScoreValue() { return scoreValue; }
    public int getGrowthDelta() { return growthDelta; }
}
