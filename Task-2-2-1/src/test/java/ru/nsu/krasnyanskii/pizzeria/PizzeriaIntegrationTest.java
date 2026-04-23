package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.nsu.krasnyanskii.pizzeria.workers.Baker;
import ru.nsu.krasnyanskii.pizzeria.workers.Courier;

/** Integration test: verifies orders travel the full pipeline from queue to delivery. */
class PizzeriaIntegrationTest {

    private final PizzeriaView view = new PizzeriaView();

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void fullPipelineDeliversAllOrders() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new PizzaStorage(5);

        Order o1 = new Order();
        Order o2 = new Order();
        Order o3 = new Order();
        queue.put(o1);
        queue.put(o2);
        queue.put(o3);
        queue.close();

        Baker b1 = new Baker(1, 50, queue, storage, view);
        Baker b2 = new Baker(2, 80, queue, storage, view);
        Thread bt1 = new Thread(b1);
        Thread bt2 = new Thread(b2);
        bt1.start();
        bt2.start();
        bt1.join(3000);
        bt2.join(3000);

        assertEquals(3, storage.size(), "All 3 pizzas should be in storage");

        storage.closeAccepting();
        Courier courier = new Courier(1, 5, 50, storage, view);
        Thread ct = new Thread(courier);
        ct.start();
        ct.join(3000);

        assertFalse(ct.isAlive());
        assertTrue(storage.isEmpty());
        assertEquals(Order.State.DELIVERED, o1.getState());
        assertEquals(Order.State.DELIVERED, o2.getState());
        assertEquals(Order.State.DELIVERED, o3.getState());
    }

    @Test
    void storageCapacityLimitBlocksBakers() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new PizzaStorage(1);

        Order o1 = new Order();
        Order o2 = new Order();
        queue.put(o1);
        queue.put(o2);
        queue.close();

        Baker baker = new Baker(1, 30, queue, storage, view);
        Thread bt = new Thread(baker);
        bt.start();

        Thread.sleep(300);
        assertEquals(1, storage.size());

        storage.closeAccepting();
        Courier courier = new Courier(1, 5, 10, storage, view);
        Thread ct = new Thread(courier);
        ct.start();

        bt.join(2000);
        ct.join(2000);
        assertFalse(bt.isAlive());
    }
}
