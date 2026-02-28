package ru.nsu.krasnyanskii.pizzeria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест: проверяет что заказы проходят весь путь
 * от очереди до доставки.
 */
class PizzeriaIntegrationTest {

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void fullPipelineDeliversAllOrders() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new PizzaStorage(5);

        // Добавляем 3 заказа
        Order o1 = new Order();
        Order o2 = new Order();
        Order o3 = new Order();
        queue.put(o1);
        queue.put(o2);
        queue.put(o3);
        queue.close();

        // Запускаем 2 пекарей
        Baker b1 = new Baker(1, 50, queue, storage);
        Baker b2 = new Baker(2, 80, queue, storage);
        Thread bt1 = new Thread(b1);
        Thread bt2 = new Thread(b2);
        bt1.start();
        bt2.start();
        bt1.join(3000);
        bt2.join(3000);

        assertEquals(3, storage.size(), "Все 3 пиццы должны быть на складе");

        // Закрываем склад и запускаем курьера
        storage.closeAccepting();
        Courier courier = new Courier(1, 5, 50, storage);
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
        // Склад на 1 пиццу, 2 заказа — второй пекарь должен подождать курьера
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        PizzaStorage storage = new PizzaStorage(1);

        Order o1 = new Order();
        Order o2 = new Order();
        queue.put(o1);
        queue.put(o2);
        queue.close();

        Baker baker = new Baker(1, 30, queue, storage);
        Thread bt = new Thread(baker);
        bt.start();

        // Даём время испечь первую и заблокироваться на второй
        Thread.sleep(300);
        assertEquals(1, storage.size());

        // Курьер освобождает место
        storage.closeAccepting();
        Courier courier = new Courier(1, 5, 10, storage);
        Thread ct = new Thread(courier);
        ct.start();

        bt.join(2000);
        ct.join(2000);

        assertFalse(bt.isAlive());
    }
}
