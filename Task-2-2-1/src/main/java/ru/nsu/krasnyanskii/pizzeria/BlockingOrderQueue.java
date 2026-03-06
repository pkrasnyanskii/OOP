package ru.nsu.krasnyanskii.pizzeria;

import java.util.LinkedList;

/**
 * Custom blocking queue implemented with intrinsic locks (wait/notifyAll).
 * Does NOT use java.util.concurrent collections.
 */
public class BlockingOrderQueue<T> {
    private final LinkedList<T> queue = new LinkedList<>();
    private final int capacity;
    private volatile boolean closed = false;

    public BlockingOrderQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() >= capacity && !closed) {
            wait();
        }
        if (closed) return;
        queue.addLast(item);
        notifyAll();
    }

    /**
     * Blocks until an item is available or the queue is closed.
     * @return item, or null if queue is closed and empty
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty() && !closed) {
            wait();
        }
        if (queue.isEmpty()) return null;
        T item = queue.removeFirst();
        notifyAll();
        return item;
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }

    public synchronized void close() {
        closed = true;
        notifyAll();
    }

    public boolean isClosed() {
        return closed;
    }

    /** Drain all remaining elements into a list (for serialization). */
    public synchronized java.util.List<T> drainAll() {
        java.util.List<T> result = new java.util.ArrayList<>(queue);
        queue.clear();
        notifyAll();
        return result;
    }
}
