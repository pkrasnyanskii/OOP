package ru.nsu.krasnyanskii.pizzeria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BakerTest {

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void bakerCooksOrderAndPutsToStorage() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new PizzaStorage(10);

        Order order = new Order();
        queue.put(order);
        queue.close(); // после этого заказа пекарь завершится

        Baker baker = new Baker(1, 50, queue, storage);
        Thread t = new Thread(baker);
        t.start();
        t.join(2000);

        assertFalse(t.isAlive(), "Пекарь должен завершиться");
        assertEquals(1, storage.size(), "Пицца должна быть на складе");
        assertEquals(Order.State.IN_STORAGE, order.getState());
    }

    @Test
    void bakerThrowsOnInvalidCookingTime() {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new PizzaStorage(10);
        assertThrows(IllegalArgumentException.class,
                () -> new Baker(1, 0, queue, storage));
    }

    @Test
    void bakerStopsWhenQueueClosedAndEmpty() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new PizzaStorage(10);
        queue.close();

        Baker baker = new Baker(1, 50, queue, storage);
        Thread t = new Thread(baker);
        t.start();
        t.join(1000);
        assertFalse(t.isAlive());
    }
}
