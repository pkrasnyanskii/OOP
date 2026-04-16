package ru.nsu.krasnyanskii.snake;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;

class DirectionTest {

    @Test
    void upOppositeDown() {
        assertTrue(Direction.UP.isOpposite(Direction.DOWN));
    }

    @Test
    void downOppositeUp() {
        assertTrue(Direction.DOWN.isOpposite(Direction.UP));
    }

    @Test
    void leftOppositeRight() {
        assertTrue(Direction.LEFT.isOpposite(Direction.RIGHT));
    }

    @Test
    void rightOppositeLeft() {
        assertTrue(Direction.RIGHT.isOpposite(Direction.LEFT));
    }

    @Test
    void upNotOppositeLeft() {
        assertFalse(Direction.UP.isOpposite(Direction.LEFT));
    }

    @Test
    void rightNotOppositeSelf() {
        assertFalse(Direction.RIGHT.isOpposite(Direction.RIGHT));
    }
}
