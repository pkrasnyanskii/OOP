package ru.nsu.krasnyanskii.pizzeria.storage;

import java.util.List;
import ru.nsu.krasnyanskii.pizzeria.model.Order;

/** Contract for thread-safe bounded pizza storage. */
public interface PizzaStorage {

    /**
     * Puts a pizza into storage, blocking if full.
     *
     * @param order cooked order to store
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    void put(Order order) throws InterruptedException;

    /**
     * Takes up to {@code maxCount} pizzas, blocking while storage is empty and open.
     *
     * @param maxCount max pizzas to take
     * @return list of taken pizzas; empty if storage is closed and empty
     * @throws InterruptedException if the thread is interrupted while waiting
     */
    List<Order> take(int maxCount) throws InterruptedException;

    /** Signals that no more pizzas will be added; couriers will drain and exit. */
    void closeAccepting();

    /**
     * Drains all remaining pizzas.
     *
     * @return snapshot of remaining orders
     */
    List<Order> drainAll();

    /**
     * Returns {@code true} if no pizzas are in storage.
     *
     * @return {@code true} if empty
     */
    boolean isEmpty();

    /**
     * Returns the number of pizzas currently in storage.
     *
     * @return current size
     */
    int size();

    /**
     * Returns {@code true} if the storage is still accepting new pizzas.
     *
     * @return {@code true} if accepting
     */
    boolean isAccepting();
}
