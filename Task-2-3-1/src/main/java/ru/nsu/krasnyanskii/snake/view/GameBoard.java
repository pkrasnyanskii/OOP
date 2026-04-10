package ru.nsu.krasnyanskii.snake.view;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import ru.nsu.krasnyanskii.snake.model.GameModel;
import ru.nsu.krasnyanskii.snake.model.GameState;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;
import ru.nsu.krasnyanskii.snake.model.entity.Food;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

public class GameBoard extends Canvas {

    private static final Color COLOR_BG_EVEN = Color.web("#1a1a2e");
    private static final Color COLOR_BG_ODD = Color.web("#16213e");
    private static final Color COLOR_SNAKE_HEAD = Color.web("#e94560");
    private static final Color COLOR_SNAKE_BODY = Color.web("#0f3460");
    private static final Color COLOR_SNAKE_OUTLINE = Color.web("#533483");
    private static final Color COLOR_FOOD_NORMAL = Color.web("#4ecca3");
    private static final Color COLOR_FOOD_BONUS = Color.web("#f5a623");
    private static final Color COLOR_FOOD_SHRINK = Color.web("#e94560");
    private static final Color COLOR_OBSTACLE = Color.web("#533483");
    private static final Color COLOR_OVERLAY = Color.color(0, 0, 0, 0.65);

    private int cellSize;
    private boolean firstRender = true;
    private final Set<Point> lastSnakePositions = new HashSet<>();

    public GameBoard(double width, double height) {
        super(width, height);
    }

    public void render(GameModel model) {
        int cols = model.getConfig().boardWidth();
        int rows = model.getConfig().boardHeight();
        cellSize = (int) Math.min(getWidth() / cols, getHeight() / rows);

        GraphicsContext gc = getGraphicsContext2D();

        if (firstRender || model.getState() != GameState.RUNNING) {
            fullRedraw(gc, model, cols, rows);
            firstRender = false;
            lastSnakePositions.clear();
            lastSnakePositions.addAll(model.getSnake().getBody());
            return;
        }

        partialRedraw(gc, model);
        lastSnakePositions.clear();
        lastSnakePositions.addAll(model.getSnake().getBody());
    }

    private void fullRedraw(GraphicsContext gc, GameModel model, int cols, int rows) {
        gc.clearRect(0, 0, getWidth(), getHeight());
        drawBackground(gc, cols, rows);
        drawObstacles(gc, model);
        drawFood(gc, model.getFoods());
        drawSnake(gc, model);

        if (model.getState() != GameState.RUNNING) {
            drawOverlay(gc, model);
        }
    }

    private void partialRedraw(GraphicsContext gc, GameModel model) {
        // Очищаем все позиции, где змейка была в прошлом кадре
        for (Point p : lastSnakePositions) {
            drawBackgroundCell(gc, p);
        }

        // Очищаем позиции еды (на случай съедания)
        for (Food food : model.getFoods()) {
            drawBackgroundCell(gc, food.getPosition());
        }

        // Рисуем актуальную еду и змейку
        drawFood(gc, model.getFoods());
        drawSnake(gc, model);
    }

    private void drawBackgroundCell(GraphicsContext gc, Point p) {
        gc.setFill((p.x() + p.y()) % 2 == 0 ? COLOR_BG_EVEN : COLOR_BG_ODD);
        gc.fillRect(p.x() * cellSize, p.y() * cellSize, cellSize, cellSize);
    }

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
        double margin = cellSize * 0.1;
        for (Point p : model.getObstacles()) {
            gc.fillRoundRect(p.x() * cellSize + margin, p.y() * cellSize + margin,
                    cellSize - margin * 2, cellSize - margin * 2, 6, 6);
        }
    }

    private void drawFood(GraphicsContext gc, List<Food> foods) {
        for (Food food : foods) {
            Color color = switch (food.getType()) {
                case NORMAL -> COLOR_FOOD_NORMAL;
                case BONUS -> COLOR_FOOD_BONUS;
                case SHRINK -> COLOR_FOOD_SHRINK;
            };
            double margin = cellSize * 0.15;
            double size = cellSize - margin * 2;
            double px = food.getPosition().x() * cellSize + margin;
            double py = food.getPosition().y() * cellSize + margin;

            gc.setFill(color);
            gc.fillOval(px, py, size, size);
            gc.setFill(Color.color(1, 1, 1, 0.35));
            gc.fillOval(px + size * 0.15, py + size * 0.1, size * 0.3, size * 0.25);
        }
    }

    private void drawSnake(GraphicsContext gc, GameModel model) {
        List<Point> body = model.getSnake().getBody();
        double margin = cellSize * 0.08;
        double inner = cellSize - margin * 2;

        for (int i = 0; i < body.size(); i++) {
            Point p = body.get(i);
            boolean isHead = (i == 0);
            double arc = isHead ? 8 : 5;
            double px = p.x() * cellSize + margin;
            double py = p.y() * cellSize + margin;

            gc.setFill(isHead ? COLOR_SNAKE_HEAD : COLOR_SNAKE_BODY);
            gc.fillRoundRect(px, py, inner, inner, arc, arc);

            gc.setStroke(COLOR_SNAKE_OUTLINE);
            gc.setLineWidth(1.5);
            gc.strokeRoundRect(px, py, inner, inner, arc, arc);

            if (isHead) {
                drawEyes(gc, p, model.getDirection());
            }
        }
    }

    private void drawEyes(GraphicsContext gc, Point head, Direction dir) {
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
        double cx = getWidth() / 2.0;
        double cy = getHeight() / 2.0;

        switch (model.getState()) {
            case GAME_OVER -> {
                gc.setFill(Color.web("#e94560"));
                gc.setFont(Font.font("Monospace", 36));
                gc.fillText("GAME OVER", cx, cy - 30);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Monospace", 18));
                gc.fillText("Score: " + model.getScore(), cx, cy + 10);
                gc.fillText("Press R to restart", cx, cy + 40);
            }
            case WIN -> {
                gc.setFill(Color.web("#4ecca3"));
                gc.setFont(Font.font("Monospace", 36));
                gc.fillText("YOU WIN!", cx, cy - 30);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Monospace", 18));
                gc.fillText("Score: " + model.getScore() + " Level: " + model.getLevel(), cx, cy + 10);
                gc.fillText("Press R to restart", cx, cy + 40);
            }
            case PAUSED -> {
                gc.setFill(Color.web("#f5a623"));
                gc.setFont(Font.font("Monospace", 36));
                gc.fillText("PAUSED", cx, cy - 10);
                gc.setFill(Color.WHITE);
                gc.setFont(Font.font("Monospace", 16));
                gc.fillText("Press P to continue", cx, cy + 30);
            }
        }
    }
}