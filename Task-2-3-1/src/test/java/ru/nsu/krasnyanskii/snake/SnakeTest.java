package ru.nsu.krasnyanskii.snake;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.Snake;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SnakeTest {

    private Snake snake;

    @BeforeEach
    void setUp() {
        snake = new Snake(new Point(5, 5));
    }

    @Test void initialLengthIsOne() { assertEquals(1, snake.getLength()); }
    @Test void initialHead()        { assertEquals(new Point(5, 5), snake.getHead()); }

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

    @Test void bodyContainsHead()             { assertTrue(snake.contains(new Point(5, 5))); }
    @Test void bodyDoesNotContainArbitraryCell() { assertFalse(snake.contains(new Point(0, 0))); }

    @Test
    void selfCollisionDetected() {
        snake.scheduleGrowth(4);
        snake.move(Direction.RIGHT);
        snake.move(Direction.DOWN);
        snake.move(Direction.LEFT);
        snake.move(Direction.UP);
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
        snake.scheduleGrowth(3);
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        snake.move(Direction.RIGHT);
        assertEquals(4, snake.getLength());
        snake.scheduleGrowth(-2);
        assertEquals(2, snake.getLength());
    }

    @Test
    void shrinkCannotKillSnake() {
        snake.scheduleGrowth(-5);
        assertEquals(1, snake.getLength());
    }

    @Test
    void getBodyHeadFirst() {
        snake.scheduleGrowth(1);
        snake.move(Direction.RIGHT);
        var body = snake.getBody();
        assertEquals(new Point(6, 5), body.get(0));
        assertEquals(new Point(5, 5), body.get(1));
    }
}
