package ru.nsu.krasnyanskii;

import ru.nsu.krasnyanskii.controller.OopCheckerController;

/**
 * Application entry point.
 *
 * <p>Usage: {@code java -jar oop-checker.jar [config-dir] [--output report.html]}
 *
 * <p>All orchestration is delegated to {@link OopCheckerController}.
 */
public class Main {

    /**
     * Starts the OOP checker.
     *
     * @param args command-line arguments forwarded to {@link OopCheckerController}
     * @throws Exception on any unrecoverable error
     */
    public static void main(String[] args) throws Exception {
        new OopCheckerController(args).run();
    }
}
