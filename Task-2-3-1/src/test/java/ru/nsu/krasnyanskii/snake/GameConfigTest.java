package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.GameConfig;

import static org.junit.jupiter.api.Assertions.*;

class GameConfigTest {

    @Test
    void defaultsAreValid() {
        GameConfig cfg = GameConfig.defaults();
        assertTrue(cfg.boardWidth()  > 0);
        assertTrue(cfg.boardHeight() > 0);
        assertTrue(cfg.foodCount()   > 0);
        assertTrue(cfg.winLength()   > 0);
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
        // Level 1000 should still respect the floor
        long tick = cfg.tickForLevel(1000);
        assertTrue(tick >= cfg.minSpeed());
    }

    @Test
    void customConfig() {
        GameConfig cfg = new GameConfig(10, 10, 2, 15, 200, 20, 60, 3);
        assertEquals(10, cfg.boardWidth());
        assertEquals(15, cfg.winLength());
        assertEquals(200, cfg.tickForLevel(1));
        assertEquals(180, cfg.tickForLevel(2));
    }
}
