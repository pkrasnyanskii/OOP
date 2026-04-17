package ru.nsu.krasnyanskii.snake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX application entry point.
 * <p>
 * Loads the FXML layout, which in turn instantiates the
 * {@link ru.nsu.krasnyanskii.snake.controller.GameController}.
 * The controller wires the model and the view together — this class has no
 * game logic of its own (single responsibility principle).
 * </p>
 */
public class SnakeApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/snake/game.fxml")
        );
        Scene scene = new Scene(loader.load());

        primaryStage.setTitle("Snake — Task_2_3_1");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
