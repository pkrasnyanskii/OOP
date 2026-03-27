package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CourierTest {

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void courierDeliversPizzas() throws InterruptedException {
        PizzaStorage storage = new PizzaStorage(10);

        Order o1 = new Order();
        Order o2 = new Order();
        storage.put(o1);
        storage.put(o2);
        storage.closeAccepting(); // курьер заберёт и завершится

        Courier courier = new Courier(1, 5, 50, storage);
        Thread t = new Thread(courier);
        t.start();
        t.join(2000);

        assertFalse(t.isAlive());
        assertEquals(Order.State.DELIVERED, o1.getState());
        assertEquals(Order.State.DELIVERED, o2.getState());
    }

    @Test
    void courierRespectsCapacity() throws InterruptedException {
        PizzaStorage storage = new PizzaStorage(10);
        for (int i = 0; i < 5; i++) {
            storage.put(new Order());
        }
        storage.closeAccepting();

        // Багажник на 2 пиццы — понадобится несколько поездок
        Courier courier = new Courier(1, 2, 10, storage);
        Thread t = new Thread(courier);
        t.start();
        t.join(3000);

        assertFalse(t.isAlive());
        assertTrue(storage.isEmpty());
    }

    @Test
    void courierThrowsOnInvalidCapacity() {
        PizzaStorage storage = new PizzaStorage(10);
        assertThrows(IllegalArgumentException.class,
                () -> new Courier(1, 0, 100, storage));
    }
}
