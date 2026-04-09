package ru.nsu.krasnyanskii.snake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

class PointTest {

    @Test
    void moveUp() {
        assertEquals(new Point(5, 4), new Point(5, 5).move(Direction.UP));
    }

    @Test
    void moveDown() {
        assertEquals(new Point(5, 6), new Point(5, 5).move(Direction.DOWN));
    }

    @Test
    void moveLeft() {
        assertEquals(new Point(4, 5), new Point(5, 5).move(Direction.LEFT));
    }

    @Test
    void moveRight() {
        assertEquals(new Point(6, 5), new Point(5, 5).move(Direction.RIGHT));
    }

    @Test
    void equalityAndHashCode() {
        Point a = new Point(3, 7);
        Point b = new Point(3, 7);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void inequalityDifferentCoordinates() {
        assertNotEquals(new Point(1, 2), new Point(2, 1));
    }
}
