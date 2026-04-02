package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.Food;
import ru.nsu.krasnyanskii.snake.model.FoodType;
import ru.nsu.krasnyanskii.snake.model.Point;

class FoodTest {

    @Test
    void foodStoresPositionAndType() {
        Point p = new Point(3, 4);
        Food food = new Food(p, FoodType.NORMAL);
        assertEquals(p, food.getPosition());
        assertEquals(FoodType.NORMAL, food.getType());
    }

    @Test
    void bonusFoodType() {
        Food food = new Food(new Point(0, 0), FoodType.BONUS);
        assertEquals(FoodType.BONUS, food.getType());
    }

    @Test
    void shrinkFoodType() {
        Food food = new Food(new Point(1, 1), FoodType.SHRINK);
        assertEquals(FoodType.SHRINK, food.getType());
    }
}
