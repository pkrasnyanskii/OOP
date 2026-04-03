package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.entity.FoodType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FoodTypeTest {

    @Test
    void normalFoodScoreAndGrowth() {
        assertEquals(1, FoodType.NORMAL.getScoreValue());
        assertEquals(1, FoodType.NORMAL.getGrowthDelta());
    }

    @Test
    void bonusFoodHigherScore() {
        assertTrue(FoodType.BONUS.getScoreValue() > FoodType.NORMAL.getScoreValue());
        assertEquals(1, FoodType.BONUS.getGrowthDelta());
    }

    @Test
    void shrinkFoodNegativeGrowthPositiveScore() {
        assertTrue(FoodType.SHRINK.getGrowthDelta() < 0);
        assertTrue(FoodType.SHRINK.getScoreValue() > 0);
    }
}
