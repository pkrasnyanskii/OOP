package ru.nsu.krasnyanskii.snake.model;

/**
 * Immutable configuration object for a game session.
 * All game-tunable parameters live here; the rest of the code reads from
 * this record, so adding a new parameter never requires hunting down
 * scattered magic numbers (open/closed principle for configuration).
 *
 * @param boardWidth   board width in cells (N)
 * @param boardHeight  board height in cells (M)
 * @param foodCount    number of food items always present on board (T)
 * @param winLength    snake length required to win (L)
 * @param initialSpeed base game tick interval in milliseconds (level 1)
 * @param speedupPerLevel milliseconds subtracted from tick per level-up
 * @param minSpeed     minimum tick interval (speed cap)
 * @param obstacleCount number of static obstacle cells on the board
 */
public record GameConfig(
        int boardWidth,
        int boardHeight,
        int foodCount,
        int winLength,
        long initialSpeed,
        long speedupPerLevel,
        long minSpeed,
        int obstacleCount
) {
    /** Convenient default configuration. */
    public static GameConfig defaults() {
        return new GameConfig(
                20,   // width
                20,   // height
                3,    // food items
                20,   // win length
                300,  // initial tick ms
                30,   // speed-up per level
                80,   // minimum tick ms
                5     // obstacles
        );
    }

    /** Returns the tick interval for the given level (1-based). */
    public long tickForLevel(int level) {
        long speed = initialSpeed - (long)(level - 1) * speedupPerLevel;
        return Math.max(speed, minSpeed);
    }
}
