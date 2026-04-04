package ru.nsu.krasnyanskii.snake.model;

/**
 * Lifecycle states of a game session.
 *
 * <p>The controller drives transitions; the view renders different overlays
 * depending on the current state.</p>
 */
public enum GameState {
    /** Game loop is running and accepting input. */
    RUNNING,
    /** Game loop is suspended until the player resumes. */
    PAUSED,
    /** Snake collided with a wall, obstacle, or itself. */
    GAME_OVER,
    /** Snake reached the target length. */
    WIN
}
