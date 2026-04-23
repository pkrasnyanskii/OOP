package ru.nsu.krasnyanskii.pizzeria;

/**
 * Abstraction for softly stoppable workers.
 *
 * <p><b>ISP</b>: single-method interface keeps it focused on one capability.</p>
 * <p><b>DIP</b>: {@code Pizzeria} depends on this abstraction instead of concrete
 * {@code Baker}/{@code Courier}/{@code OrderGenerator} classes.</p>
 */
public interface Stoppable {

    /** Requests the worker to finish after completing its current task. */
    void stop();
}
