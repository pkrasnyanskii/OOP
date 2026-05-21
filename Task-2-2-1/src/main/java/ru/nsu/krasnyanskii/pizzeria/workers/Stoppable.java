package ru.nsu.krasnyanskii.pizzeria.workers;

/** Contract for softly stoppable workers. */
public interface Stoppable {

    /** Requests the worker to finish after completing its current task. */
    void stop();
}
