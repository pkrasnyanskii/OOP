package ru.nsu.krasnyanskii.snake.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import ru.nsu.krasnyanskii.snake.model.Direction;
import ru.nsu.krasnyanskii.snake.model.GameConfig;
import ru.nsu.krasnyanskii.snake.model.GameModel;
import ru.nsu.krasnyanskii.snake.model.GameState;
import ru.nsu.krasnyanskii.snake.view.GameBoard;

/**
 * MVC Controller — bridges the model and view.
 * <p>
 * Responsibilities:
 *   <li>Owns the {@link AnimationTimer} game loop.</li>
 *   <li>Translates keyboard events into model direction requests.</li>
 *   <li>Advances the model via {@link GameModel#step()} according to level speed.</li>
 *   <li>Requests the view ({@link GameBoard}) to redraw after each step.</li>
 *   <li>Updates the HUD labels (score, level, length).</li>
 * </p>
 *
 * <h3>Why AnimationTimer instead of a Thread?</h3>
 * {@code AnimationTimer} callbacks run on the JavaFX Application Thread,
 * so we never need Platform.runLater() for UI updates. We control the
 * effective tick rate manually using nanosecond timestamps.
 */
public class GameController {

    @FXML private BorderPane rootPane;
    @FXML private Label labelScore;
    @FXML private Label labelLevel;
    @FXML private Label labelLength;

    private GameBoard  board;
    private GameModel  model;
    private GameConfig config;
    private AnimationTimer gameLoop;

    /** Timestamp (ns) of the last game-logic tick. */
    private long lastTickTime = 0;

    /**
     * Called by the FXML loader after all @FXML fields are injected.
     * We create the canvas here (not in FXML) so we can size it to the pane.
     */
    @FXML
    public void initialize() {
        config = GameConfig.defaults();
        setupBoard();
        startNewGame();
    }

    private void setupBoard() {
        double boardPx = 500;
        board = new GameBoard(boardPx, boardPx);
        rootPane.setCenter(board);

        rootPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnKeyPressed(this::handleKey);
            }
        });
    }

    private void startNewGame() {
        if (gameLoop != null) gameLoop.stop();

        model = new GameModel(config);
        lastTickTime = 0;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long nowNano) {
                long tickMs = config.tickForLevel(model.getLevel());
                long tickNano = tickMs * 1_000_000L;

                if (lastTickTime == 0) {
                    lastTickTime = nowNano;
                }

                if (nowNano - lastTickTime >= tickNano) {
                    lastTickTime = nowNano;
                    model.step();
                    updateHUD();
                }
                board.render(model);
            }
        };
        gameLoop.start();
    }

    private void handleKey(KeyEvent event) {
        KeyCode code = event.getCode();
        switch (code) {
            case UP,    W -> model.requestDirection(Direction.UP);
            case DOWN,  S -> model.requestDirection(Direction.DOWN);
            case LEFT,  A -> model.requestDirection(Direction.LEFT);
            case RIGHT, D -> model.requestDirection(Direction.RIGHT);
            case P, ESCAPE -> model.togglePause();
            case R -> {
                if (model.getState() != GameState.RUNNING &&
                    model.getState() != GameState.PAUSED) {
                    startNewGame();
                }
            }
        }
        event.consume();
    }

    private void updateHUD() {
        labelScore.setText("Score: " + model.getScore());
        labelLevel.setText("Level: " + model.getLevel());
        labelLength.setText("Length: " + model.getSnake().getLength()
                + " / " + config.winLength());
    }
}
