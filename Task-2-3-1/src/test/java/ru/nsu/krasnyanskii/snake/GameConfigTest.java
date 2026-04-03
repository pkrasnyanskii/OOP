package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.GameConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameConfigTest {

    @Test
    void defaultsAreValid() {
        GameConfig cfg = GameConfig.defaults();
        assertTrue(cfg.boardWidth()   > 0);
        assertTrue(cfg.boardHeight()  > 0);
        assertTrue(cfg.foodCount()    > 0);
        assertTrue(cfg.winLength()    > 0);
        assertTrue(cfg.initialSpeed() > 0);
    }

    @Test
    void tickForLevelOneEqualsInitialSpeed() {
        GameConfig cfg = GameConfig.defaults();
        assertEquals(cfg.initialSpeed(), cfg.tickForLevel(1));
    }

    @Test
    void tickForHigherLevelIsSmaller() {
        GameConfig cfg = GameConfig.defaults();
        assertTrue(cfg.tickForLevel(2) < cfg.tickForLevel(1));
    }

    @Test
    void tickNeverGoesBelowMinSpeed() {
        GameConfig cfg = GameConfig.defaults();
        assertTrue(cfg.tickForLevel(1000) >= cfg.minSpeed());
    }

    @Test
    void withWrapAroundReturnsCopyWithFlagSet() {
        GameConfig solid = GameConfig.defaults();
        GameConfig wrap  = solid.withWrapAround(true);
        assertTrue(wrap.wrapAround());
        assertEquals(solid.boardWidth(), wrap.boardWidth());
    }
}
