package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.Direction;
import ru.nsu.krasnyanskii.snake.model.Point;
import ru.nsu.krasnyanskii.snake.model.Snake;

class SnakeTest {

    private Snake snake;

    @BeforeEach
    void setUp() {
        // Snake starts at (5, 5), heading RIGHT by default
        snake = new Snake(new Point(5, 5));
    }

    @Test
    void initialLengthIsOne() {
        assertEquals(1, snake.getLength());
    }

    @Test
    void initialHead() {
        assertEquals(new Point(5, 5), snake.getHead());
    }

    @Test
    void moveRightAdvancesHead() {
        snake.move(Direction.RIGHT);
        assertEquals(new Point(6, 5), snake.getHead());
    }

    @Test
    void moveDoesNotGrowWithoutScheduledGrowth() {
        snake.move(Direction.RIGHT);
        assertEquals(1, snake.getLength());
    }

    @Test
    void scheduleGrowthIncreasesLength() {
        snake.scheduleGrowth(1);
        snake.move(Direction.RIGHT);
        assertEquals(2, snake.getLength());
    }

    @Test
    void scheduleGrowthThreeSteps() {
        snake.scheduleGrowth(3);
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        assertEquals(4, snake.getLength());
    }

    @Test
    void snakeBodyContainsHead() {
        assertTrue(snake.contains(new Point(5, 5)));
    }

    @Test
    void snakeBodyDoesNotContainArbitraryCell() {
        assertFalse(snake.contains(new Point(0, 0)));
    }

    @Test
    void selfCollisionDetected() {
        // Build a snake of length 5, then loop it back into itself
        snake.scheduleGrowth(4);
        snake.move(Direction.RIGHT); // (6,5)
        snake.move(Direction.DOWN);  // (6,6)
        snake.move(Direction.LEFT);  // (5,6)
        snake.move(Direction.UP);    // (5,5) — collides with original head
        assertTrue(snake.selfCollision());
    }

    @Test
    void noSelfCollisionOnNormalMove() {
        snake.scheduleGrowth(2);
        snake.move(Direction.RIGHT);
        snake.move(Direction.DOWN);
        assertFalse(snake.selfCollision());
    }

    @Test
    void shrinkReducesLength() {
        // Grow to 4 first
        snake.scheduleGrowth(3);
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        assertEquals(4, snake.getLength());

        // Now shrink by 2
        snake.scheduleGrowth(-2);
        assertEquals(2, snake.getLength());
    }

    @Test
    void shrinkCannotKillSnake() {
        // Length is 1; shrink by 5 should keep length at 1
        snake.scheduleGrowth(-5);
        assertEquals(1, snake.getLength());
    }

    @Test
    void getBodyHeadFirst() {
        snake.scheduleGrowth(1);
        snake.move(Direction.RIGHT); // head=(6,5), tail=(5,5)
        var body = snake.getBody();
        assertEquals(new Point(6, 5), body.get(0)); // head first
        assertEquals(new Point(5, 5), body.get(1)); // tail second
    }
}
