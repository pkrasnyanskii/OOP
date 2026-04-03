package ru.nsu.krasnyanskii.snake.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import ru.nsu.krasnyanskii.snake.model.board.BoardBounds;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;
import ru.nsu.krasnyanskii.snake.model.entity.Food;
import ru.nsu.krasnyanskii.snake.model.entity.FoodType;
import ru.nsu.krasnyanskii.snake.model.entity.Point;

/**
 * Central model for the Snake game.
 *
 * <p>Owns all mutable game state: the {@link Snake}, active {@link Food} items,
 * static obstacle positions, score, level, and the current {@link GameState}.</p>
 *
 * <p>This class is completely free of JavaFX. It exposes a single
 * {@link #step()} method that the controller calls once per game tick, plus
 * query methods the view uses for rendering.</p>
 *
 * <p>Wall behaviour is fully delegated to the {@link BoardBounds} strategy
 * supplied at construction time. Switching between solid walls and wrap-around
 * requires no change to this class (open/closed principle).</p>
 */
public class GameModel {

    private final GameConfig  config;
    private final BoardBounds bounds;
    private final Random      random = new Random();

    private final Snake       snake;
    private final List<Food>  foods     = new ArrayList<>();
    private final Set<Point>  obstacles = new HashSet<>();

    private Direction currentDirection = Direction.RIGHT;
    private Direction pendingDirection = Direction.RIGHT;

    private GameState state          = GameState.RUNNING;
    private int       score          = 0;
    private int       level          = 1;
    private int       nextLevelScore = 10;

    /**
     * Constructs a new game from the given configuration.
     *
     * <p>The appropriate {@link BoardBounds} implementation is created via
     * {@link GameConfig#createBounds()}, so the caller never needs to
     * instantiate boundary objects directly.</p>
     *
     * @param config the game configuration to use
     */
    public GameModel(GameConfig config) {
        this.config = config;
        this.bounds = config.createBounds();

        Point start = new Point(config.boardWidth() / 2, config.boardHeight() / 2);
        this.snake  = new Snake(start);

        spawnObstacles();
        replenishFood();
    }

    /**
     * Advances the game by one tick.
     *   Apply the buffered direction (reverse moves are silently ignored).
     *   Compute the new head via {@link BoardBounds#apply(Point)}.
     *   If the result is {@code null}, the snake hit a solid wall → {@link GameState#GAME_OVER}.
     *   Check obstacle and self-collision.
     *   Check food consumption → grow snake and update score.
     *   Check win condition.
     *   Replenish food to maintain {@link GameConfig#foodCount()} items on the board.
     *
     * <p>This method is a no-op when the state is not {@link GameState#RUNNING}.</p>
     */
    public void step() {
        if (state != GameState.RUNNING) return;

        if (!pendingDirection.isOpposite(currentDirection)) {
            currentDirection = pendingDirection;
        }

        Point rawHead  = snake.getHead().move(currentDirection);
        Point safeHead = bounds.apply(rawHead);

        if (safeHead == null) {
            state = GameState.GAME_OVER;
            return;
        }

        if (!safeHead.equals(rawHead)) {
            snake.teleportHead(safeHead);
        } else {
            snake.move(currentDirection);
        }

        if (obstacles.contains(safeHead)) {
            state = GameState.GAME_OVER;
            return;
        }

        if (snake.selfCollision()) {
            state = GameState.GAME_OVER;
            return;
        }

        Food eaten = findFoodAt(safeHead);
        if (eaten != null) {
            foods.remove(eaten);
            snake.scheduleGrowth(eaten.getType().getGrowthDelta());
            score += eaten.getType().getScoreValue();
            checkLevelUp();
        }

        if (snake.getLength() >= config.winLength()) {
            state = GameState.WIN;
            return;
        }

        replenishFood();
    }

    /**
     * Buffers a direction change to be applied at the start of the next {@link #step()}.
     *
     * <p>Reverse moves (e.g. requesting {@link Direction#LEFT} while moving
     * {@link Direction#RIGHT}) are silently ignored both here and in {@link #step()}.</p>
     *
     * @param dir the requested direction
     */
    public void requestDirection(Direction dir) {
        if (!dir.isOpposite(currentDirection)) {
            pendingDirection = dir;
        }
    }

    /**
     * Toggles between {@link GameState#RUNNING} and {@link GameState#PAUSED}.
     * Has no effect when the game is over or won.
     */
    public void togglePause() {
        if (state == GameState.RUNNING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.RUNNING;
        }
    }

    private Food findFoodAt(Point position) {
        for (Food food : foods) {
            if (food.getPosition().equals(position)) {
                return food;
            }
        }
        return null;
    }

    private void spawnObstacles() {
        Point snakeStart = snake.getHead();
        while (obstacles.size() < config.obstacleCount()) {
            Point p = randomFreeCell();
            if (Math.abs(p.x() - snakeStart.x()) > 3
                    || Math.abs(p.y() - snakeStart.y()) > 3) {
                obstacles.add(p);
            }
        }
    }

    private void replenishFood() {
        while (foods.size() < config.foodCount()) {
            foods.add(new Food(randomFreeCell(), pickFoodType()));
        }
    }

    private FoodType pickFoodType() {
        int roll = random.nextInt(100);
        if (roll < 70) return FoodType.NORMAL;
        if (roll < 90) return FoodType.BONUS;
        return FoodType.SHRINK;
    }

    private Point randomFreeCell() {
        Set<Point> occupied = new HashSet<>(obstacles);
        snake.getBody().forEach(occupied::add);
        foods.forEach(f -> occupied.add(f.getPosition()));

        Point p;
        do {
            p = new Point(random.nextInt(config.boardWidth()),
                          random.nextInt(config.boardHeight()));
        } while (occupied.contains(p));
        return p;
    }

    private void checkLevelUp() {
        if (score >= nextLevelScore) {
            level++;
            nextLevelScore += 10 + level * 5;
        }
    }

    /** @return the snake entity */
    public Snake       getSnake()     { return snake; }

    /** @return unmodifiable view of active food items */
    public List<Food>  getFoods()     { return Collections.unmodifiableList(foods); }

    /** @return unmodifiable view of obstacle cell positions */
    public Set<Point>  getObstacles() { return Collections.unmodifiableSet(obstacles); }

    /** @return current game lifecycle state */
    public GameState   getState()     { return state; }

    /** @return accumulated score */
    public int         getScore()     { return score; }

    /** @return current level (1-based) */
    public int         getLevel()     { return level; }

    /** @return the configuration this game was started with */
    public GameConfig  getConfig()    { return config; }

    /** @return the direction the snake is currently travelling */
    public Direction   getDirection() { return currentDirection; }
}
