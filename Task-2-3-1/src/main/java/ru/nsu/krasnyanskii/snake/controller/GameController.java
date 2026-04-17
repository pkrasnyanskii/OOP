package ru.nsu.krasnyanskii.snake.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import ru.nsu.krasnyanskii.snake.model.GameConfig;
import ru.nsu.krasnyanskii.snake.model.GameModel;
import ru.nsu.krasnyanskii.snake.model.GameState;
import ru.nsu.krasnyanskii.snake.model.entity.Direction;
import ru.nsu.krasnyanskii.snake.view.GameBoard;

/**
 * MVC Controller — wires the model and the view together.
 *
 * <p>Responsibilities:</p>
 *   Owns the {@link AnimationTimer} that drives the game loop.
 *   Translates {@link KeyEvent}s into model direction requests and game commands.
 *   Advances the model via {@link GameModel#step()} at a rate controlled by the current level.
 *   Triggers a full canvas redraw via {@link GameBoard#render(GameModel)} every frame.
 *   Keeps the HUD labels (score, level, length) up to date after each tick.
 *
 * <p>{@link AnimationTimer} is used instead of a background {@link Thread} because its
 * {@code handle()} callback always runs on the JavaFX Application Thread, eliminating
 * the need for {@code Platform.runLater()} around every UI update. Tick rate is
 * managed manually via nanosecond timestamps rather than {@code Thread.sleep()}.</p>
 */
public class GameController {

    /** Root layout pane injected from FXML. */
    @FXML private BorderPane rootPane;

    /** HUD label displaying the current score. */
    @FXML private Label labelScore;

    /** HUD label displaying the current level. */
    @FXML private Label labelLevel;

    /** HUD label displaying the current snake length and win target. */
    @FXML private Label labelLength;

    /** Canvas-based view component placed in the center of {@link #rootPane}. */
    private GameBoard board;

    /** The game model holding all mutable game state. */
    private GameModel model;

    /** Immutable configuration for the current game session. */
    private GameConfig config;

    /** JavaFX timer that drives the game loop at display refresh rate. */
    private AnimationTimer gameLoop;

    /** Nanosecond timestamp of the last game-logic tick. */
    private long lastTickNano = 0;

    /**
     * Called automatically by the FXML loader after all {@code @FXML} fields are injected.
     * Sets up the canvas and starts the first game session.
     */
    @FXML
    public void initialize() {
        config = GameConfig.defaults();
        setupBoard();
        startNewGame();
    }

    /**
     * Creates the {@link GameBoard} canvas, places it in the centre of the root pane,
     * and attaches the keyboard handler to the {@link javafx.scene.Scene} so that key
     * events are received regardless of which node currently holds focus.
     */
    private void setupBoard() {
        board = new GameBoard(500, 500);
        rootPane.setCenter(board);

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKey);
            }
        });
    }

    /**
     * Stops any running game loop, creates a fresh {@link GameModel}, and starts a new
     * {@link AnimationTimer} that ticks the model and redraws the board every frame.
     */
    private void startNewGame() {
        if (gameLoop != null) {
            gameLoop.stop();
        }

        model        = new GameModel(config);
        lastTickNano = 0;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(final long nowNano) {
                long tickNano = config.tickForLevel(model.getLevel()) * 1_000_000L;

                if (lastTickNano == 0) {
                    lastTickNano = nowNano;
                }

                if (nowNano - lastTickNano >= tickNano) {
                    lastTickNano = nowNano;
                    model.step();
                    updateHud();
                }

                board.render(model);
            }
        };
        gameLoop.start();
    }

    /**
     * Translates a {@link KeyEvent} into a model direction request or game command.
     *   Arrow keys / WASD — request a direction change.
     *   P / Escape — toggle pause.
     *   R — restart (only when the game is over or won).
     *
     * @param event the key event fired by the scene
     */
    private void handleKey(final KeyEvent event) {
        switch (event.getCode()) {
            case UP,    W      -> model.requestDirection(Direction.UP);
            case DOWN,  S      -> model.requestDirection(Direction.DOWN);
            case LEFT,  A      -> model.requestDirection(Direction.LEFT);
            case RIGHT, D      -> model.requestDirection(Direction.RIGHT);
            case P,     ESCAPE -> model.togglePause();
            case R -> {
                if (model.getState() != GameState.RUNNING
                        && model.getState() != GameState.PAUSED) {
                    startNewGame();
                }
            }
            default -> { }
        }
        event.consume();
    }

    /**
     * Updates the three HUD labels with the current score, level, and snake length.
     * Called once per game-logic tick.
     */
    private void updateHud() {
        labelScore.setText("Score: "   + model.getScore());
        labelLevel.setText("Level: "   + model.getLevel());
        labelLength.setText(
                "Length: " + model.getSnake().getLength()
                        + " / "    + config.winLength()
        );
    }
}