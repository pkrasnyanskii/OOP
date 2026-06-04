package ru.nsu.krasnyanskii.pizzeria.workers;

/**
 * Marker interface for a pizzeria worker.
 *
 * <p>Workers are cooperative {@link Runnable}s; they finish when their input
 * dries up (queue/storage closed) or when their thread is interrupted.</p>
 */
public interface Worker extends Runnable {
}
