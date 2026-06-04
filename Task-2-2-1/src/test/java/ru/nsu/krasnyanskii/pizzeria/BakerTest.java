package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.queue.BlockingOrderQueue;
import ru.nsu.krasnyanskii.pizzeria.queue.OrderQueue;
import ru.nsu.krasnyanskii.pizzeria.storage.BoundedPizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.storage.PizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.view.ConsolePizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.workers.Baker;

class BakerTest {

    private final PizzeriaView view = new ConsolePizzeriaView();

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void bakerCooksOrderAndPutsToStorage() throws InterruptedException {
        OrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new BoundedPizzaStorage(10);

        Order order = new Order();
        queue.put(order);
        queue.close();

        Baker baker = new Baker(1, 50, queue, storage, view);
        Thread t = new Thread(baker);
        t.start();
        t.join(2000);

        assertFalse(t.isAlive(), "Baker should finish");
        assertEquals(1, storage.size(), "Pizza should be in storage");
        assertEquals(Order.State.IN_STORAGE, order.getState());
    }

    @Test
    void bakerThrowsOnInvalidCookingTime() {
        OrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new BoundedPizzaStorage(10);
        assertThrows(IllegalArgumentException.class,
                () -> new Baker(1, 0, queue, storage, view));
    }

    @Test
    void bakerStopsWhenQueueClosedAndEmpty() throws InterruptedException {
        OrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new BoundedPizzaStorage(10);
        queue.close();

        Baker baker = new Baker(1, 50, queue, storage, view);
        Thread t = new Thread(baker);
        t.start();
        t.join(1000);
        assertFalse(t.isAlive());
    }
}
