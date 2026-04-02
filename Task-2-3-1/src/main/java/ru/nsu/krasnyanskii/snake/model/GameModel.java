package ru.nsu.krasnyanskii.snake.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Central model for the Snake game.
 * <p>
 * Holds all mutable game state:
 * <ul>
 *   <li>The {@link Snake} body</li>
 *   <li>Active {@link Food} items</li>
 *   <li>Static obstacle positions</li>
 *   <li>Score, level, and {@link GameState}</li>
 * </ul>
 * This class is <em>completely free of JavaFX</em>; it communicates with
 * the controller via plain return values and is testable with pure JUnit.
 * </p>
 *
 * <h3>MVC role</h3>
 * GameModel = <strong>M</strong>. It enforces all game rules and exposes
 * query methods. The controller calls {@link #step()} once per tick,
 * reads the resulting state, and notifies the view to redraw.
 */
public class GameModel {

    // ------------------------------------------------------------------ //
    //  State                                                               //
    // ------------------------------------------------------------------ //

    private final GameConfig config;
    private final Random random = new Random();

    private Snake snake;
    private Direction currentDirection = Direction.RIGHT;
    private Direction pendingDirection = Direction.RIGHT;

    private final List<Food>  foods     = new ArrayList<>();
    private final Set<Point>  obstacles = new HashSet<>();

    private GameState state = GameState.RUNNING;
    private int score  = 0;
    private int level  = 1;
    /** Accumulated score threshold to trigger next level-up. */
    private int nextLevelScore = 10;

    // ------------------------------------------------------------------ //
    //  Constructor                                                         //
    // ------------------------------------------------------------------ //

    public GameModel(GameConfig config) {
        this.config = config;
        Point start = new Point(config.boardWidth() / 2, config.boardHeight() / 2);
        snake = new Snake(start);
        spawnObstacles();
        replenishFood();
    }

    // ------------------------------------------------------------------ //
    //  Game loop step — called by the controller timer                     //
    // ------------------------------------------------------------------ //

    /**
     * Advances the game by one tick.
     * <ol>
     *   <li>Apply the buffered direction change.</li>
     *   <li>Move the snake.</li>
     *   <li>Check wall/obstacle/self collisions → GAME_OVER.</li>
     *   <li>Check food consumption → grow + score.</li>
     *   <li>Check win condition.</li>
     *   <li>Replenish missing food items.</li>
     * </ol>
     */
    public void step() {
        if (state != GameState.RUNNING) return;

        // Apply buffered direction (cannot reverse)
        if (!pendingDirection.isOpposite(currentDirection)) {
            currentDirection = pendingDirection;
        }

        // Move
        snake.move(currentDirection);

        // --- Collision checks ---
        Point head = snake.getHead();

        // Wall collision
        if (head.x() < 0 || head.x() >= config.boardWidth()
                || head.y() < 0 || head.y() >= config.boardHeight()) {
            state = GameState.GAME_OVER;
            return;
        }

        // Obstacle collision
        if (obstacles.contains(head)) {
            state = GameState.GAME_OVER;
            return;
        }

        // Self-collision
        if (snake.selfCollision()) {
            state = GameState.GAME_OVER;
            return;
        }

        // --- Food consumption ---
        Food eaten = null;
        for (Food food : foods) {
            if (food.getPosition().equals(head)) {
                eaten = food;
                break;
            }
        }
        if (eaten != null) {
            foods.remove(eaten);
            snake.scheduleGrowth(eaten.getType().getGrowthDelta());
            score += eaten.getType().getScoreValue();
            checkLevelUp();
        }

        // --- Win condition ---
        if (snake.getLength() >= config.winLength()) {
            state = GameState.WIN;
            return;
        }

        // Keep food count constant
        replenishFood();
    }

    // ------------------------------------------------------------------ //
    //  Direction input                                                     //
    // ------------------------------------------------------------------ //

    /**
     * Buffers a requested direction change.
     * The actual change is applied at the start of the next {@link #step()},
     * preventing the snake from reversing mid-tick.
     */
    public void requestDirection(Direction dir) {
        if (!dir.isOpposite(currentDirection)) {
            pendingDirection = dir;
        }
    }

    // ------------------------------------------------------------------ //
    //  Pause / resume                                                      //
    // ------------------------------------------------------------------ //

    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    // ------------------------------------------------------------------ //
    //  Internal helpers                                                    //
    // ------------------------------------------------------------------ //

    private void spawnObstacles() {
        int count = config.obstacleCount();
        Point snakeStart = snake.getHead();
        while (obstacles.size() < count) {
            Point p = randomFreeCell();
            // Keep a clear zone of 3 around the spawn point
            if (Math.abs(p.x() - snakeStart.x()) > 3 || Math.abs(p.y() - snakeStart.y()) > 3) {
                obstacles.add(p);
            }
        }
    }

    private void replenishFood() {
        while (foods.size() < config.foodCount()) {
            Point p = randomFreeCell();
            FoodType type = pickFoodType();
            foods.add(new Food(p, type));
        }
    }

    /**
     * Weighted random food type: 70% NORMAL, 20% BONUS, 10% SHRINK.
     */
    private FoodType pickFoodType() {
        int roll = random.nextInt(100);
        if (roll < 70) return FoodType.NORMAL;
        if (roll < 90) return FoodType.BONUS;
        return FoodType.SHRINK;
    }

    /**
     * Finds a random cell that is not occupied by the snake, food, or obstacles.
     */
    private Point randomFreeCell() {
        Set<Point> occupied = new HashSet<>(obstacles);
        snake.getBody().forEach(occupied::add);
        foods.forEach(f -> occupied.add(f.getPosition()));

        Point p;
        do {
            int x = random.nextInt(config.boardWidth());
            int y = random.nextInt(config.boardHeight());
            p = new Point(x, y);
        } while (occupied.contains(p));
        return p;
    }

    private void checkLevelUp() {
        if (score >= nextLevelScore) {
            level++;
            nextLevelScore += 10 + level * 5;
        }
    }

    // ------------------------------------------------------------------ //
    //  Accessors (read-only views for controller/view)                    //
    // ------------------------------------------------------------------ //

    public Snake          getSnake()      { return snake; }
    public List<Food>     getFoods()      { return Collections.unmodifiableList(foods); }
    public Set<Point>     getObstacles()  { return Collections.unmodifiableSet(obstacles); }
    public GameState      getState()      { return state; }
    public int            getScore()      { return score; }
    public int            getLevel()      { return level; }
    public GameConfig     getConfig()     { return config; }
    public Direction      getDirection()  { return currentDirection; }
}
