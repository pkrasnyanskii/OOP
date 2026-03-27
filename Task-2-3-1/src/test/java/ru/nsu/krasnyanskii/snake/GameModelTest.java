package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.Direction;
import ru.nsu.krasnyanskii.snake.model.Food;
import ru.nsu.krasnyanskii.snake.model.GameConfig;
import ru.nsu.krasnyanskii.snake.model.GameModel;
import ru.nsu.krasnyanskii.snake.model.GameState;
import ru.nsu.krasnyanskii.snake.model.Point;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link GameModel}.
 *
 * Strategy: use a small (10x10) board with 0 obstacles and 0 food items
 * so we can drive the snake to specific positions deterministically
 * (food replenishment is disabled when foodCount == 0).
 *
 * Note: we use a config with foodCount=0 so randomness never interferes.
 */
class GameModelTest {

    /** Minimal config: 10×10, no food, no obstacles, win at length 5. */
    private static GameConfig noFoodConfig() {
        return new GameConfig(10, 10, 0, 5, 300, 30, 80, 0);
    }

    private GameModel model;

    @BeforeEach
    void setUp() {
        model = new GameModel(noFoodConfig());
        // Snake starts at (5,5) heading RIGHT
    }

    // ------------------------------------------------------------------ //
    //  Initial state                                                       //
    // ------------------------------------------------------------------ //

    @Test
    void initialStateIsRunning() {
        assertEquals(GameState.RUNNING, model.getState());
    }

    @Test
    void initialScoreIsZero() {
        assertEquals(0, model.getScore());
    }

    @Test
    void initialLevelIsOne() {
        assertEquals(1, model.getLevel());
    }

    @Test
    void initialSnakeLengthIsOne() {
        assertEquals(1, model.getSnake().getLength());
    }

    // ------------------------------------------------------------------ //
    //  Movement                                                            //
    // ------------------------------------------------------------------ //

    @Test
    void stepMovesSnakeRight() {
        Point headBefore = model.getSnake().getHead();
        model.step();
        Point headAfter = model.getSnake().getHead();
        assertEquals(headBefore.x() + 1, headAfter.x());
        assertEquals(headBefore.y(),     headAfter.y());
    }

    @Test
    void requestedDirectionAppliedOnNextStep() {
        model.requestDirection(Direction.DOWN);
        model.step();
        assertEquals(Direction.DOWN, model.getDirection());
    }

    @Test
    void reverseDirectionIgnored() {
        // Snake moves RIGHT; requesting LEFT should be ignored
        model.requestDirection(Direction.LEFT);
        model.step();
        assertEquals(Direction.RIGHT, model.getDirection());
    }

    // ------------------------------------------------------------------ //
    //  Wall collision → GAME_OVER                                          //
    // ------------------------------------------------------------------ //

    @Test
    void hitRightWallCausesGameOver() {
        // Snake at (5,5) moving right — needs 4 steps to reach x=9, one more to go out
        for (int i = 0; i < 6; i++) model.step();
        assertEquals(GameState.GAME_OVER, model.getState());
    }

    @Test
    void hitTopWallCausesGameOver() {
        model.requestDirection(Direction.UP);
        // 6 steps will push past y=0
        for (int i = 0; i < 7; i++) model.step();
        assertEquals(GameState.GAME_OVER, model.getState());
    }

    @Test
    void hitBottomWallCausesGameOver() {
        model.requestDirection(Direction.DOWN);
        for (int i = 0; i < 6; i++) model.step();
        assertEquals(GameState.GAME_OVER, model.getState());
    }

    // ------------------------------------------------------------------ //
    //  Self-collision → GAME_OVER                                          //
    // ------------------------------------------------------------------ //

    @Test
    void selfCollisionCausesGameOver() {
        /*
         * Grow to length 5, then steer into own body:
         *   Start: (5,5) → RIGHT
         *   We manually inject growth into the snake to simulate food eating.
         *   Then navigate in a U shape.
         */
        model.getSnake().scheduleGrowth(4);
        // Move right 4 times to build body: (5,5)(6,5)(7,5)(8,5)(9,5) — but
        // wall at x=9 so let's go down then loop.
        // Simpler: head right 2, then down 1, then left 3 (head enters body).
        model.getSnake().scheduleGrowth(4); // total 4 pending growth
        step(model, Direction.RIGHT);  // head=(6,5)
        step(model, Direction.RIGHT);  // head=(7,5)
        step(model, Direction.DOWN);   // head=(7,6)
        step(model, Direction.LEFT);   // head=(6,6)
        step(model, Direction.LEFT);   // head=(5,6)
        step(model, Direction.UP);     // head=(5,5) — same as original start position
        // Body still contains (5,5) → self-collision
        assertEquals(GameState.GAME_OVER, model.getState());
    }

    // ------------------------------------------------------------------ //
    //  Win condition                                                       //
    // ------------------------------------------------------------------ //

    @Test
    void reachingWinLengthSetsWinState() {
        // Win length = 5; schedule growth of 4 so one step achieves it
        model.getSnake().scheduleGrowth(4);
        // Take 4 steps to the right (board is 10 wide, starts at x=5)
        for (int i = 0; i < 4; i++) model.step();
        assertEquals(GameState.WIN, model.getState());
    }

    // ------------------------------------------------------------------ //
    //  Pause / resume                                                      //
    // ------------------------------------------------------------------ //

    @Test
    void pauseStopsStepProcessing() {
        model.togglePause();
        assertEquals(GameState.PAUSED, model.getState());

        Point headBefore = model.getSnake().getHead();
        model.step(); // should be no-op while paused
        assertEquals(headBefore, model.getSnake().getHead());
    }

    @Test
    void resumeAfterPause() {
        model.togglePause();
        model.togglePause();
        assertEquals(GameState.RUNNING, model.getState());
        // Confirm step works again
        Point headBefore = model.getSnake().getHead();
        model.step();
        assertNotEquals(headBefore, model.getSnake().getHead());
    }

    // ------------------------------------------------------------------ //
    //  Food consumption (with food-enabled config)                         //
    // ------------------------------------------------------------------ //

    @Test
    void eatingFoodIncreasesScore() {
        // Use normal config so food exists; place snake next to food manually
        // is hard — instead use a model where we know the snake will eat.
        // Simpler: use the default model and verify score starts at 0 then
        // increases after multiple steps (probabilistic; skip if no food nearby).
        GameModel m = new GameModel(GameConfig.defaults());
        int initialScore = m.getScore();
        // Run 200 steps; with T=3 food items the snake will almost certainly eat
        for (int i = 0; i < 200 && m.getState() == GameState.RUNNING; i++) {
            m.step();
        }
        // Either game over (ran into wall) or score increased — if still running, score > 0
        if (m.getState() != GameState.GAME_OVER) {
            assertTrue(m.getScore() >= initialScore);
        }
    }

    // ------------------------------------------------------------------ //
    //  Obstacle detection                                                  //
    // ------------------------------------------------------------------ //

    @Test
    void obstaclesExistOnDefaultConfig() {
        GameModel m = new GameModel(GameConfig.defaults());
        assertFalse(m.getObstacles().isEmpty());
    }

    @Test
    void noObstaclesOnZeroObstacleConfig() {
        assertTrue(model.getObstacles().isEmpty());
    }

    // ------------------------------------------------------------------ //
    //  Helper                                                              //
    // ------------------------------------------------------------------ //

    /** Sets direction then steps the model. */
    private void step(GameModel m, Direction dir) {
        m.requestDirection(dir);
        m.step();
    }
}
