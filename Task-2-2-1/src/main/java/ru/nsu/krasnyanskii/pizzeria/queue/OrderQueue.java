package ru.nsu.krasnyanskii.pizzeria.queue;

import java.util.List;

/** Contract for a bounded blocking queue of orders. */
public interface OrderQueue<T> {

    /**
     * Inserts an element, blocking until space is available.
     *
     * @param item element to insert
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    void put(T item) throws InterruptedException;

    /**
     * Retrieves and removes the head element, blocking until one is available.
     *
     * @return the head element, or {@code null} if the queue is closed and empty
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    T take() throws InterruptedException;

    /** Closes the queue; threads blocked in {@code take()} will receive {@code null}. */
    void close();

    /**
     * Returns {@code true} if the queue has been closed.
     *
     * @return {@code true} if closed
     */
    boolean isClosed();

    /**
     * Drains all remaining elements.
     *
     * @return snapshot of remaining elements
     */
    List<T> drainAll();

    /**
     * Returns {@code true} if the queue contains no elements.
     *
     * @return {@code true} if empty
     */
    boolean isEmpty();

    /**
     * Returns the number of elements currently in the queue.
     *
     * @return current size
     */
    int size();
}
