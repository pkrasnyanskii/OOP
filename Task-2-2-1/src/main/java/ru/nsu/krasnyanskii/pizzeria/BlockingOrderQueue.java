package ru.nsu.krasnyanskii.pizzeria;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Bounded blocking queue backed by intrinsic locks ({@code wait}/{@code notifyAll}).
 *
 * <p>Uses manual synchronization because {@code java.util.concurrent.BlockingQueue}
 * is prohibited by the assignment.</p>
 *
 * @param <T> element type
 */
public class BlockingOrderQueue<T> {

    private final Deque<T> queue = new ArrayDeque<>();
    private final int capacity;
    private volatile boolean closed = false;

    /**
     * Creates a bounded blocking queue.
     *
     * @param capacity max elements; must be positive
     * @throws IllegalArgumentException if capacity is not positive
     */
    public BlockingOrderQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Inserts an element, blocking until space is available.
     *
     * @param item element to insert
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() >= capacity && !closed) {
            wait();
        }
        if (closed) {
            return;
        }
        queue.addLast(item);
        notifyAll();
    }

    /**
     * Retrieves and removes the head element, blocking until one is available.
     *
     * @return the head element, or {@code null} if the queue is closed and empty
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty() && !closed) {
            wait();
        }
        if (queue.isEmpty()) {
            return null;
        }
        T item = queue.removeFirst();
        notifyAll();
        return item;
    }

    /** Closes the queue; threads blocked in {@code take()} will receive {@code null}. */
    public synchronized void close() {
        closed = true;
        notifyAll();
    }

    /**
     * Drains all remaining elements (used for serialization on shutdown).
     *
     * @return snapshot of remaining elements
     */
    public synchronized List<T> drainAll() {
        List<T> result = new ArrayList<>(queue);
        queue.clear();
        notifyAll();
        return result;
    }

    /**
     * Returns {@code true} if the queue contains no elements.
     *
     * @return {@code true} if empty
     */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns the number of elements currently in the queue.
     *
     * @return current size
     */
    public synchronized int size() {
        return queue.size();
    }

    /**
     * Returns {@code true} if the queue has been closed.
     *
     * @return {@code true} if closed
     */
    public boolean isClosed() {
        return closed;
    }
}
