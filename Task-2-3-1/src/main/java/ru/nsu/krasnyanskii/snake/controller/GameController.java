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
 *   Owns the {@link AnimationTimer} that drives the game loop.</li>
 *   Translates {@link javafx.scene.input.KeyEvent}s into model direction requests and game commands.
 *   Advances the model via {@link GameModel#step()} at a rate controlled by the current level.
 *   Triggers a full canvas redraw via {@link GameBoard#render(GameModel)} every animation frame.
 *   Keeps the HUD labels (score, level, length) up to date after each tick.
 *
 * <p>{@link AnimationTimer} is used instead of a background {@link Thread} because its
 * {@code handle()} callback always runs on the JavaFX Application Thread, eliminating
 * the need for {@code Platform.runLater()} around every UI update. Tick rate is
 * managed manually via nanosecond timestamps rather than {@code Thread.sleep()}.</p>
 */
public class GameController {

    @FXML private BorderPane rootPane;
    @FXML private Label      labelScore;
    @FXML private Label      labelLevel;
    @FXML private Label      labelLength;

    private GameBoard     board;
    private GameModel     model;
    private GameConfig    config;
    private AnimationTimer gameLoop;
    private long          lastTickNano = 0;

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

    private void setupBoard() {
        board = new GameBoard(500, 500);
        rootPane.setCenter(board);

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKey);
            }
        });
    }

    private void startNewGame() {
        if (gameLoop != null) gameLoop.stop();

        model        = new GameModel(config);
        lastTickNano = 0;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long nowNano) {
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

    private void handleKey(KeyEvent event) {
        switch (event.getCode()) {
            case UP,    W -> model.requestDirection(Direction.UP);
            case DOWN,  S -> model.requestDirection(Direction.DOWN);
            case LEFT,  A -> model.requestDirection(Direction.LEFT);
            case RIGHT, D -> model.requestDirection(Direction.RIGHT);
            case P, ESCAPE -> model.togglePause();
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

    private void updateHud() {
        labelScore.setText("Score: "   + model.getScore());
        labelLevel.setText("Level: "   + model.getLevel());
        labelLength.setText("Length: " + model.getSnake().getLength()
                + " / " + config.winLength());
    }
}
