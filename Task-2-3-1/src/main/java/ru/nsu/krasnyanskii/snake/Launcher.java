package ru.nsu.krasnyanskii.snake;

/**
 * Launcher — точка входа, отделённая от {@link SnakeApplication}.
 *
 * <p>Проблема: если главный класс напрямую наследует {@code javafx.application.Application},
 * JVM при запуске через {@code java -jar} проверяет его суперкласс <em>до</em> инициализации
 * модульной системы и выбрасывает «JavaFX runtime components are missing».
 * Решение: запускать через промежуточный класс без наследования JavaFX,
 * который делегирует вызов {@code Application.launch()} уже внутри метода.</p>
 */
public class Launcher {
    public static void main(String[] args) {
        SnakeApplication.main(args);
    }
}
