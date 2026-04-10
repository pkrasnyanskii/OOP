package ru.nsu.krasnyanskii.snake;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.snake.model.board.BoardBounds;
import ru.nsu.krasnyanskii.snake.model.board.SolidWallBounds;
import ru.nsu.krasnyanskii.snake.model.board.WrapAroundBounds;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

class BoardBoundsTest {

    private static final int W = 10;
    private static final int H = 10;

    @Test
    void solidWallReturnsPointInsideBoard() {
        BoardBounds b = new SolidWallBounds(W, H);
        assertEquals(new Point(5, 5), b.apply(new Point(5, 5)));
    }

    @Test
    void solidWallReturnsNullForNegativeX() {
        assertNull(new SolidWallBounds(W, H).apply(new Point(-1, 5)));
    }

    @Test
    void solidWallReturnsNullForOverflowX() {
        assertNull(new SolidWallBounds(W, H).apply(new Point(10, 5)));
    }

    @Test
    void solidWallReturnsNullForNegativeY() {
        assertNull(new SolidWallBounds(W, H).apply(new Point(5, -1)));
    }

    @Test
    void solidWallReturnsNullForOverflowY() {
        assertNull(new SolidWallBounds(W, H).apply(new Point(5, 10)));
    }

    @Test
    void wrapAroundNeverReturnsNull() {
        BoardBounds b = new WrapAroundBounds(W, H);
        for (int x = -2; x <= W + 2; x++) {
            for (int y = -2; y <= H + 2; y++) {
                Point result = b.apply(new Point(x, y));
                assertNotNull(result);
            }
        }
    }

    @Test
    void wrapAroundExitRightEntersLeft() {
        assertEquals(new Point(0, 5), new WrapAroundBounds(W, H).apply(new Point(10, 5)));
    }

    @Test
    void wrapAroundExitLeftEntersRight() {
        assertEquals(new Point(9, 5), new WrapAroundBounds(W, H).apply(new Point(-1, 5)));
    }

    @Test
    void wrapAroundExitBottomEntersTop() {
        assertEquals(new Point(5, 0), new WrapAroundBounds(W, H).apply(new Point(5, 10)));
    }

    @Test
    void wrapAroundExitTopEntersBottom() {
        assertEquals(new Point(5, 9), new WrapAroundBounds(W, H).apply(new Point(5, -1)));
    }

    @Test
    void wrapAroundPointInsideBoardUnchanged() {
        assertEquals(new Point(3, 7), new WrapAroundBounds(W, H).apply(new Point(3, 7)));
    }
}
