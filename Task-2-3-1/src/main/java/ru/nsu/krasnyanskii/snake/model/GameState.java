package ru.nsu.krasnyanskii.snake.model;

/**
 * Represents the lifecycle state of a game session.
 * The controller transitions between these states; the view renders
 * different overlays depending on the current state.
 */
public enum GameState {
    /** Game is active and the game loop is running. */
    RUNNING,
    /** Game is paused; loop is halted until resumed. */
    PAUSED,
    /** Snake hit a wall, itself, or an obstacle — player lost. */
    GAME_OVER,
    /** Snake reached win length — player won. */
    WIN
}
