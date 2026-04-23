package ru.nsu.krasnyanskii.pizzeria;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Bounded blocking queue backed by intrinsic locks ({@code wait}/{@code notifyAll}).
 *
 * <p>Uses manual synchronization because {@code java.util.concurrent.BlockingQueue}
 * is prohibited by the assignment.</p>
 *
 * <h3>synchronized variants</h3>
 * <ul>
 *   <li><b>synchronized method</b> — lock on the instance ({@code this}).</li>
 *   <li><b>synchronized block</b> — lock on an explicit object; narrows the critical
 *       section or allows separate locks for independent data.</li>
 *   <li><b>synchronized static method</b> — lock on the {@code Class} object; one lock
 *       per class across the entire JVM, regardless of instance count.</li>
 * </ul>
 *
 * @param <T> element type
 */
public class BlockingOrderQueue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
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
     * <p>{@link InterruptedException} is thrown when a thread blocked in
     * {@code wait}/{@code sleep}/{@code join} receives {@code thread.interrupt()}.
     * This is Java's cooperative cancellation mechanism — the thread must either
     * re-throw the exception or restore the flag via
     * {@code Thread.currentThread().interrupt()}.</p>
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
     * <p>For {@link InterruptedException} semantics see {@link #put(Object)}.</p>
     *
     * @return the head element, or {@code null} if the queue is closed and empty
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty() && !closed) {
            wait();
        }
        if (queue.isEmpty()) {
            return null; // closed and empty — signal for workers to stop
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

    /** Returns {@code true} if the queue contains no elements. */
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    /** Returns the number of elements currently in the queue. */
    public synchronized int size() {
        return queue.size();
    }

    /** Returns {@code true} if the queue has been closed. */
    public boolean isClosed() {
        return closed;
    }
}
