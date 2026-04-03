package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.GameConfig;
import ru.nsu.krasnyanskii.snake.model.GameModel;
import ru.nsu.krasnyanskii.snake.model.GameState;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for {@link GameModel}.
 *
 * <p>A minimal 10×10 board with zero food and zero obstacles is used throughout
 * so that random elements never interfere with deterministic assertions.</p>
 */
class GameModelTest {

    /** 10×10, no food, no obstacles, solid walls, win at length 5. */
    private static GameConfig minimalConfig() {
        return new GameConfig(10, 10, 0, 5, 300, 30, 80, 0, false);
    }

    private GameModel model;

    @BeforeEach
    void setUp() {
        model = new GameModel(minimalConfig());
    }

    @Test void initialStateIsRunning()   { assertEquals(GameState.RUNNING, model.getState()); }
    @Test void initialScoreIsZero()      { assertEquals(0, model.getScore()); }
    @Test void initialLevelIsOne()       { assertEquals(1, model.getLevel()); }
    @Test void initialSnakeLengthIsOne() { assertEquals(1, model.getSnake().getLength()); }

    @Test
    void stepMovesSnakeRight() {
        Point before = model.getSnake().getHead();
        model.step();
        assertEquals(before.x() + 1, model.getSnake().getHead().x());
        assertEquals(before.y(),     model.getSnake().getHead().y());
    }

    @Test
    void requestedDirectionAppliedOnNextStep() {
        model.requestDirection(Direction.DOWN);
        model.step();
        assertEquals(Direction.DOWN, model.getDirection());
    }

    @Test
    void reverseDirectionIgnored() {
        model.requestDirection(Direction.LEFT);
        model.step();
        assertEquals(Direction.RIGHT, model.getDirection());
    }

    @Test
    void hitRightWallCausesGameOver() {
        for (int i = 0; i < 6; i++) model.step();
        assertEquals(GameState.GAME_OVER, model.getState());
    }

    @Test
    void hitTopWallCausesGameOver() {
        model.requestDirection(Direction.UP);
        for (int i = 0; i < 7; i++) model.step();
        assertEquals(GameState.GAME_OVER, model.getState());
    }

    @Test
    void selfCollisionCausesGameOver() {
        model.getSnake().scheduleGrowth(4);
        step(Direction.RIGHT);
        step(Direction.DOWN);
        step(Direction.LEFT);
        step(Direction.UP);
        assertEquals(GameState.GAME_OVER, model.getState());
    }

    @Test
    void reachingWinLengthSetsWinState() {
        model.getSnake().scheduleGrowth(4);
        for (int i = 0; i < 4; i++) model.step();
        assertEquals(GameState.WIN, model.getState());
    }

    @Test
    void pauseStopsStepProcessing() {
        model.togglePause();
        assertEquals(GameState.PAUSED, model.getState());
        Point before = model.getSnake().getHead();
        model.step();
        assertEquals(before, model.getSnake().getHead());
    }

    @Test
    void resumeAfterPause() {
        model.togglePause();
        model.togglePause();
        assertEquals(GameState.RUNNING, model.getState());
        Point before = model.getSnake().getHead();
        model.step();
        assertNotEquals(before, model.getSnake().getHead());
    }

    @Test
    void noObstaclesOnZeroObstacleConfig() {
        assertTrue(model.getObstacles().isEmpty());
    }

    @Test
    void obstaclesExistOnDefaultConfig() {
        assertFalse(new GameModel(GameConfig.defaults()).getObstacles().isEmpty());
    }

    @Test
    void wrapAroundPreventsGameOverAtRightWall() {
        GameModel wrapModel = new GameModel(
                new GameConfig(10, 10, 0, 50, 300, 30, 80, 0, true));
        for (int i = 0; i < 12; i++) wrapModel.step();
        assertNotEquals(GameState.GAME_OVER, wrapModel.getState());
    }

    @Test
    void wrapAroundMovesSnakeToOppositeEdge() {
        GameModel wrapModel = new GameModel(
                new GameConfig(10, 10, 0, 50, 300, 30, 80, 0, true));
        for (int i = 0; i < 6; i++) wrapModel.step();
        int x = wrapModel.getSnake().getHead().x();
        assertTrue(x >= 0 && x < 10, "Head should be inside board after wrap");
    }

    private void step(Direction dir) {
        model.requestDirection(dir);
        model.step();
    }
}
