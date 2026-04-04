package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.entity.Food;
import ru.nsu.krasnyanskii.snake.model.entity.FoodType;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FoodTest {

    @Test
    void foodStoresPositionAndType() {
        Point p    = new Point(3, 4);
        Food  food = new Food(p, FoodType.NORMAL);
        assertEquals(p, food.getPosition());
        assertEquals(FoodType.NORMAL, food.getType());
    }

    @Test void bonusFoodType()  { assertEquals(FoodType.BONUS,  new Food(new Point(0, 0), FoodType.BONUS).getType()); }
    @Test void shrinkFoodType() { assertEquals(FoodType.SHRINK, new Food(new Point(1, 1), FoodType.SHRINK).getType()); }
}
