package ru.nsu.krasnyanskii.snake.view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import ru.nsu.krasnyanskii.snake.model.Food;
import ru.nsu.krasnyanskii.snake.model.FoodType;
import ru.nsu.krasnyanskii.snake.model.GameModel;
import ru.nsu.krasnyanskii.snake.model.GameState;
import ru.nsu.krasnyanskii.snake.model.Point;

import java.util.List;

/**
 * The view component responsible for rendering the entire game board.
 * <p>
 * Extends {@link Canvas} so it can be embedded directly in the FXML scene graph.
 * The only public entry point is {@link #render(GameModel)}, which the controller
 * calls once per tick — the view is fully passive (pure pull model).
 * </p>
 *
 * <h3>MVC role</h3>
 * GameBoard = <strong>V</strong>. It never modifies the model; it only reads it.
 */
public class GameBoard extends Canvas {

    private static final Color COLOR_BG_EVEN   = Color.web("#1a1a2e");
    private static final Color COLOR_BG_ODD    = Color.web("#16213e");
    private static final Color COLOR_SNAKE_HEAD = Color.web("#e94560");
    private static final Color COLOR_SNAKE_BODY = Color.web("#0f3460");
    private static final Color COLOR_SNAKE_OUTLINE = Color.web("#533483");
    private static final Color COLOR_FOOD_NORMAL = Color.web("#4ecca3");
    private static final Color COLOR_FOOD_BONUS  = Color.web("#f5a623");
    private static final Color COLOR_FOOD_SHRINK = Color.web("#e94560");
    private static final Color COLOR_OBSTACLE    = Color.web("#533483");
    private static final Color COLOR_OVERLAY     = Color.color(0, 0, 0, 0.65);
    private static final Color COLOR_TEXT        = Color.WHITE;

    private int cellSize;

    public GameBoard(double width, double height) {
        super(width, height);
    }

    // ------------------------------------------------------------------ //
    //  Main render entry point                                             //
    // ------------------------------------------------------------------ //

    /**
     * Redraws the entire canvas based on current model state.
     * Called on the JavaFX Application Thread by the controller.
     */
    public void render(GameModel model) {
        int cols = model.getConfig().boardWidth();
        int rows = model.getConfig().boardHeight();
        cellSize = (int) Math.min(getWidth() / cols, getHeight() / rows);

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());

        drawBackground(gc, cols, rows);
        drawObstacles(gc, model);
        drawFood(gc, model.getFoods());
        drawSnake(gc, model);

        if (model.getState() != GameState.RUNNING) {
            drawOverlay(gc, model);
        }
    }

    // ------------------------------------------------------------------ //
    //  Drawing methods                                                     //
    // ------------------------------------------------------------------ //

    private void drawBackground(GraphicsContext gc, int cols, int rows) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                gc.setFill((x + y) % 2 == 0 ? COLOR_BG_EVEN : COLOR_BG_ODD);
                gc.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }
    }

    private void drawObstacles(GraphicsContext gc, GameModel model) {
        gc.setFill(COLOR_OBSTACLE);
        for (Point p : model.getObstacles()) {
            double margin = cellSize * 0.1;
            gc.fillRoundRect(
                    p.x() * cellSize + margin,
                    p.y() * cellSize + margin,
                    cellSize - margin * 2,
                    cellSize - margin * 2,
                    6, 6
            );
        }
    }

    private void drawFood(GraphicsContext gc, List<Food> foods) {
        for (Food food : foods) {
            Color color = switch (food.getType()) {
                case NORMAL -> COLOR_FOOD_NORMAL;
                case BONUS  -> COLOR_FOOD_BONUS;
                case SHRINK -> COLOR_FOOD_SHRINK;
            };
            gc.setFill(color);
            double margin = cellSize * 0.15;
            double size   = cellSize - margin * 2;
            // Draw as circle
            gc.fillOval(
                    food.getPosition().x() * cellSize + margin,
                    food.getPosition().y() * cellSize + margin,
                    size, size
            );
            // Highlight dot
            gc.setFill(Color.color(1, 1, 1, 0.35));
            gc.fillOval(
                    food.getPosition().x() * cellSize + margin + size * 0.15,
                    food.getPosition().y() * cellSize + margin + size * 0.1,
                    size * 0.3, size * 0.25
            );
        }
    }

    private void drawSnake(GraphicsContext gc, GameModel model) {
        List<Point> body = model.getSnake().getBody();
        double margin  = cellSize * 0.08;
        double inner   = cellSize - margin * 2;

        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            boolean isHead = (i == 0);

            gc.setFill(isHead ? COLOR_SNAKE_HEAD : COLOR_SNAKE_BODY);
            gc.fillRoundRect(
                    p.x() * cellSize + margin,
                    p.y() * cellSize + margin,
                    inner, inner,
                    isHead ? 8 : 5,
                    isHead ? 8 : 5
            );

            // Segment outline for depth
            gc.setStroke(COLOR_SNAKE_OUTLINE);
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(
                    p.x() * cellSize + margin,
                    p.y() * cellSize + margin,
                    inner, inner,
                    isHead ? 8 : 5,
                    isHead ? 8 : 5
            );

            // Eyes on head
            if (isHead) {
                drawEyes(gc, p, model.getDirection());
            }
        }
    }

    private void drawEyes(GraphicsContext gc, Point head, ru.nsu.krasnyanskii.snake.model.Direction dir) {
        double cx = head.x() * cellSize + cellSize / 2.0;
        double cy = head.y() * cellSize + cellSize / 2.0;
        double eyeR = cellSize * 0.1;
        double offset = cellSize * 0.18;

        double[] ex = new double[2];
        double[] ey = new double[2];
        switch (dir) {
            case RIGHT -> { ex[0] = cx + offset; ey[0] = cy - offset; ex[1] = cx + offset; ey[1] = cy + offset; }
            case LEFT  -> { ex[0] = cx - offset; ey[0] = cy - offset; ex[1] = cx - offset; ey[1] = cy + offset; }
            case UP    -> { ex[0] = cx - offset; ey[0] = cy - offset; ex[1] = cx + offset; ey[1] = cy - offset; }
            case DOWN  -> { ex[0] = cx - offset; ey[0] = cy + offset; ex[1] = cx + offset; ey[1] = cy + offset; }
        }
        gc.setFill(Color.WHITE);
        for (int i = 0; i < 2; i++) {
            gc.fillOval(ex[i] - eyeR, ey[i] - eyeR, eyeR * 2, eyeR * 2);
        }
        gc.setFill(Color.BLACK);
        double pupilR = eyeR * 0.55;
        for (int i = 0; i < 2; i++) {
            gc.fillOval(ex[i] - pupilR, ey[i] - pupilR, pupilR * 2, pupilR * 2);
        }
    }

    private void drawOverlay(GraphicsContext gc, GameModel model) {
        gc.setFill(COLOR_OVERLAY);
        gc.fillRect(0, 0, getWidth(), getHeight());

        gc.setTextAlign(TextAlignment.CENTER);
        double cx = getWidth() / 2;

        switch (model.getState()) {
            case GAME_OVER -> {
                gc.setFill(Color.web("#e94560"));
                gc.setFont(Font.font("Monospace", 36));
                gc.fillText("GAME OVER", cx, getHeight() / 2 - 30);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Monospace", 18));
                gc.fillText("Score: " + model.getScore(), cx, getHeight() / 2 + 10);
                gc.fillText("Press R to restart", cx, getHeight() / 2 + 40);
            }
            case WIN -> {
                gc.setFill(Color.web("#4ecca3"));
                gc.setFont(Font.font("Monospace", 36));
                gc.fillText("YOU WIN!", cx, getHeight() / 2 - 30);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Monospace", 18));
                gc.fillText("Score: " + model.getScore() + "  Level: " + model.getLevel(), cx, getHeight() / 2 + 10);
                gc.fillText("Press R to restart", cx, getHeight() / 2 + 40);
            }
            case PAUSED -> {
                gc.setFill(Color.web("#f5a623"));
                gc.setFont(Font.font("Monospace", 36));
                gc.fillText("PAUSED", cx, getHeight() / 2 - 10);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Monospace", 16));
                gc.fillText("Press P to continue", cx, getHeight() / 2 + 30);
            }
        }
    }

    // ------------------------------------------------------------------ //
    //  Utility                                                             //
    // ------------------------------------------------------------------ //

    public int getCellSize() { return cellSize; }
}
