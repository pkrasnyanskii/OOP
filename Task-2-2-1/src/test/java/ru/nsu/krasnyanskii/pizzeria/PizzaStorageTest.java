package ru.nsu.krasnyanskii.pizzeria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PizzaStorageTest {

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void putAndTake() throws InterruptedException {
        PizzaStorage storage = new PizzaStorage(5);
        Order order = new Order();
        storage.put(order);
        assertEquals(1, storage.size());

        List<Order> taken = storage.take(3);
        assertEquals(1, taken.size());
        assertEquals(order.getId(), taken.get(0).getId());
        assertEquals(0, storage.size());
    }

    @Test
    void takeLimitedByTrunkCapacity() throws InterruptedException {
        PizzaStorage storage = new PizzaStorage(10);
        for (int i = 0; i < 5; i++) {
            storage.put(new Order());
        }
        List<Order> taken = storage.take(3);
        assertEquals(3, taken.size());
        assertEquals(2, storage.size());
    }

    @Test
    void takeReturnsEmptyWhenClosedAndEmpty() throws InterruptedException {
        PizzaStorage storage = new PizzaStorage(5);
        storage.closeAccepting();
        List<Order> taken = storage.take(5);
        assertTrue(taken.isEmpty());
    }

    @Test
    void putBlocksWhenFull() throws InterruptedException {
        PizzaStorage storage = new PizzaStorage(1);
        storage.put(new Order());

        Thread putter = new Thread(() -> {
            try {
                storage.put(new Order());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        putter.start();
        Thread.sleep(100);
        assertTrue(putter.isAlive(), "Пекарь должен ждать пока склад полон");

        storage.take(1);
        putter.join(500);
        assertFalse(putter.isAlive());
    }

    @Test
    void drainAll() throws InterruptedException {
        PizzaStorage storage = new PizzaStorage(10);
        storage.put(new Order());
        storage.put(new Order());
        List<Order> drained = storage.drainAll();
        assertEquals(2, drained.size());
        assertTrue(storage.isEmpty());
    }

    @Test
    void constructorThrowsOnInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new PizzaStorage(0));
    }
}
