package ru.nsu.krasnyanskii.pizzeria;

import lombok.experimental.UtilityClass;
import ru.nsu.krasnyanskii.pizzeria.controller.PizzeriaController;

/**
 * Application entry point.
 *
 * <p>All orchestration is delegated to {@link PizzeriaController}.
 */
@UtilityClass
public class Pizzeria {

    /**
     * Starts the pizzeria.
     *
     * @param args optional: {@code args[0]} config path, {@code args[1]} serialized orders path
     * @throws Exception if config cannot be loaded or threads are interrupted
     */
    public void main(String[] args) throws Exception {
        new PizzeriaController(args).run();
    }
}
