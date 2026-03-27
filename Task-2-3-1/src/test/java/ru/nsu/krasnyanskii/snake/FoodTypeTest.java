package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.FoodType;

import static org.junit.jupiter.api.Assertions.*;

class FoodTypeTest {

    @Test
    void normalFoodScoreAndGrowth() {
        assertEquals(1, FoodType.NORMAL.getScoreValue());
        assertEquals(1, FoodType.NORMAL.getGrowthDelta());
    }

    @Test
    void bonusFoodHigherScore() {
        assertTrue(FoodType.BONUS.getScoreValue() > FoodType.NORMAL.getScoreValue());
        assertEquals(1, FoodType.BONUS.getGrowthDelta()); // still grows
    }

    @Test
    void shrinkFoodNegativeGrowth() {
        assertTrue(FoodType.SHRINK.getGrowthDelta() < 0);
        assertTrue(FoodType.SHRINK.getScoreValue() > 0); // risk reward
    }
}
