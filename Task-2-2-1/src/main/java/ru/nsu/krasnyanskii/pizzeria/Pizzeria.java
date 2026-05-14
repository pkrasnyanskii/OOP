package ru.nsu.krasnyanskii.pizzeria;

import ru.nsu.krasnyanskii.pizzeria.controller.PizzeriaController;

/**
 * Application entry point.
 *
 * <p>All orchestration is delegated to {@link PizzeriaController}.
 */
public class Pizzeria {

    private Pizzeria() {
    }

    /**
     * Starts the pizzeria.
     *
     * @param args optional: {@code args[0]} config path, {@code args[1]} serialized orders path
     * @throws Exception if config cannot be loaded or threads are interrupted
     */
    public static void main(String[] args) throws Exception {
        new PizzeriaController(args).run();
    }
}
