package ru.nsu.krasnyanskii.pizzeria.queue;

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
public class BlockingOrderQueue<T> implements OrderQueue<T> {

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

    @Override
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

    @Override
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

    @Override
    public synchronized void close() {
        closed = true;
        notifyAll();
    }

    @Override
    public synchronized List<T> drainAll() {
        List<T> result = new ArrayList<>(queue);
        queue.clear();
        notifyAll();
        return result;
    }

    @Override
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public synchronized int size() {
        return queue.size();
    }

    @Override
    public boolean isClosed() {
        return closed;
    }
}
