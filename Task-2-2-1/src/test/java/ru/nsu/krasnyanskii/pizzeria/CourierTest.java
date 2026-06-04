package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.storage.BoundedPizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.storage.PizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.view.ConsolePizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.workers.Courier;

class CourierTest {

    private final PizzeriaView view = new ConsolePizzeriaView();

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void courierDeliversPizzas() throws InterruptedException {
        PizzaStorage storage = new BoundedPizzaStorage(10);

        Order o1 = new Order();
        Order o2 = new Order();
        storage.put(o1);
        storage.put(o2);
        storage.closeAccepting();

        Courier courier = new Courier(1, 5, 50, storage, view);
        Thread t = new Thread(courier);
        t.start();
        t.join(2000);

        assertFalse(t.isAlive());
        assertEquals(Order.State.DELIVERED, o1.getState());
        assertEquals(Order.State.DELIVERED, o2.getState());
    }

    @Test
    void courierRespectsCapacity() throws InterruptedException {
        PizzaStorage storage = new BoundedPizzaStorage(10);
        for (int i = 0; i < 5; i++) {
            storage.put(new Order());
        }
        storage.closeAccepting();

        Courier courier = new Courier(1, 2, 10, storage, view);
        Thread t = new Thread(courier);
        t.start();
        t.join(3000);

        assertFalse(t.isAlive());
        assertTrue(storage.isEmpty());
    }

    @Test
    void courierThrowsOnInvalidCapacity() {
        PizzaStorage storage = new BoundedPizzaStorage(10);
        assertThrows(IllegalArgumentException.class,
                () -> new Courier(1, 0, 100, storage, view));
    }
}
