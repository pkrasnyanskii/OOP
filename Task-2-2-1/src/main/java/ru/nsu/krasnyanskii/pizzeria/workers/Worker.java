package ru.nsu.krasnyanskii.pizzeria.workers;

/** Contract for a pizzeria worker: can be run in a thread and stopped gracefully. */
public interface Worker extends Runnable, Stoppable {
}
