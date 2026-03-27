package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.Direction;
import ru.nsu.krasnyanskii.snake.model.Point;

import static org.junit.jupiter.api.Assertions.*;

class PointTest {

    @Test
    void moveUp() {
        Point p = new Point(5, 5);
        assertEquals(new Point(5, 4), p.move(Direction.UP));
    }

    @Test
    void moveDown() {
        Point p = new Point(5, 5);
        assertEquals(new Point(5, 6), p.move(Direction.DOWN));
    }

    @Test
    void moveLeft() {
        Point p = new Point(5, 5);
        assertEquals(new Point(4, 5), p.move(Direction.LEFT));
    }

    @Test
    void moveRight() {
        Point p = new Point(5, 5);
        assertEquals(new Point(6, 5), p.move(Direction.RIGHT));
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
