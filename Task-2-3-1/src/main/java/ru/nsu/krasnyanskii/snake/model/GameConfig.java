package ru.nsu.krasnyanskii.snake.model;

import ru.nsu.krasnyanskii.snake.model.board.BoardBounds;
import ru.nsu.krasnyanskii.snake.model.board.SolidWallBounds;
import ru.nsu.krasnyanskii.snake.model.board.WrapAroundBounds;

/**
 * Immutable configuration snapshot for one game session.
 *
 * <p>All tunable parameters live here so the rest of the codebase never
 * contains scattered magic numbers. Adding a new parameter is a single-line
 * change in this record and in {@link #defaults()}.</p>
 *
 * @param boardWidth      board width in cells (N)
 * @param boardHeight     board height in cells (M)
 * @param foodCount       number of food items kept on the board at all times (T)
 * @param winLength       snake length required to win (L)
 * @param initialSpeed    base tick interval in milliseconds at level 1
 * @param speedupPerLevel milliseconds removed from the tick interval per level-up
 * @param minSpeed        minimum tick interval (speed cap, in milliseconds)
 * @param obstacleCount   number of static obstacle cells placed at game start
 * @param wrapAround      when {@code true} the snake passes through walls;
 *                        when {@code false} hitting a wall ends the game
 */
public record GameConfig(
        int     boardWidth,
        int     boardHeight,
        int     foodCount,
        int     winLength,
        long    initialSpeed,
        long    speedupPerLevel,
        long    minSpeed,
        int     obstacleCount,
        boolean wrapAround
) {
    /** Default configuration used when no custom config is supplied. */
    public static GameConfig defaults() {
        return new GameConfig(
                20,    // boardWidth
                20,    // boardHeight
                3,     // foodCount
                20,    // winLength
                300L,  // initialSpeed ms
                30L,   // speedupPerLevel ms
                80L,   // minSpeed ms
                5,     // obstacleCount
                true   // wrapAround
        );
    }

    /**
     * Returns a copy of this config with {@code wrapAround} toggled.
     *
     * @param wrap {@code true} for toroidal board, {@code false} for solid walls
     * @return a new {@link GameConfig} instance
     */
    public GameConfig withWrapAround(boolean wrap) {
        return new GameConfig(
                boardWidth, boardHeight, foodCount, winLength,
                initialSpeed, speedupPerLevel, minSpeed, obstacleCount,
                wrap
        );
    }

    /**
     * Calculates the tick interval for the given level.
     *
     * @param level 1-based level number
     * @return tick interval in milliseconds, never below {@link #minSpeed}
     */
    public long tickForLevel(int level) {
        long speed = initialSpeed - (long) (level - 1) * speedupPerLevel;
        return Math.max(speed, minSpeed);
    }

    /**
     * Constructs the correct {@link BoardBounds} implementation for this config.
     *
     * @return {@link WrapAroundBounds} when {@code wrapAround} is {@code true},
     *         {@link SolidWallBounds} otherwise
     */
    public BoardBounds createBounds() {
        return wrapAround
                ? new WrapAroundBounds(boardWidth, boardHeight)
                : new SolidWallBounds(boardWidth, boardHeight);
    }
}