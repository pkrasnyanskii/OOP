package ru.nsu.krasnyanskii.pizzeria;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Thread-safe bounded storage for cooked pizzas.
 *
 * <p>Uses intrinsic lock ({@code synchronized} + {@code wait}/{@code notifyAll})
 * without {@code java.util.concurrent} collections.</p>
 */
public class PizzaStorage {

    private final int capacity;
    private final LinkedList<Order> orders = new LinkedList<>();
    private volatile boolean accepting = true;

    /**
     * Creates a PizzaStorage.
     *
     * @param capacity max number of pizzas; must be positive
     * @throws IllegalArgumentException if capacity is not positive
     */
    public PizzaStorage(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Storage capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Puts a pizza into storage, blocking if full.
     *
     * @param order cooked order to store
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized void put(Order order) throws InterruptedException {
        while (orders.size() >= capacity) {
            wait();
        }
        orders.addLast(order);
        order.setState(Order.State.IN_STORAGE);
        notifyAll();
    }

    /**
     * Takes up to {@code maxCount} pizzas, blocking while storage is empty and open.
     *
     * @param maxCount max pizzas to take
     * @return list of taken pizzas; empty if storage is closed and empty
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    public synchronized List<Order> take(int maxCount) throws InterruptedException {
        while (orders.isEmpty() && accepting) {
            wait();
        }
        List<Order> taken = new ArrayList<>();
        int count = Math.min(maxCount, orders.size());
        for (int i = 0; i < count; i++) {
            taken.add(orders.removeFirst());
        }
        notifyAll();
        return taken;
    }

    /** Signals that no more pizzas will be added; couriers will drain and exit. */
    public synchronized void closeAccepting() {
        accepting = false;
        notifyAll();
    }

    /**
     * Drains all remaining pizzas (used during shutdown serialization).
     *
     * @return snapshot of remaining orders
     */
    public synchronized List<Order> drainAll() {
        List<Order> result = new ArrayList<>(orders);
        orders.clear();
        notifyAll();
        return result;
    }

    /**
     * Returns {@code true} if no pizzas are in storage.
     *
     * @return {@code true} if empty
     */
    public synchronized boolean isEmpty() {
        return orders.isEmpty();
    }

    /**
     * Returns the number of pizzas currently in storage.
     *
     * @return current size
     */
    public synchronized int size() {
        return orders.size();
    }

    /**
     * Returns {@code true} if the storage is still accepting new pizzas.
     *
     * @return {@code true} if accepting
     */
    public boolean isAccepting() {
        return accepting;
    }
}
