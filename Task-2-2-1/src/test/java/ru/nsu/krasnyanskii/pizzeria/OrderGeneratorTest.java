package ru.nsu.krasnyanskii.pizzeria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderGeneratorTest {

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void generatesOrdersIntoQueue() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        OrderGenerator generator = new OrderGenerator(queue, 50);

        Thread t = new Thread(generator);
        t.start();

        // Ждём пока появятся хотя бы 2 заказа
        Thread.sleep(200);
        generator.stop();
        t.interrupt();
        t.join(500);

        assertTrue(queue.size() >= 2, "Должно быть сгенерировано минимум 2 заказа");
    }

    @Test
    void stopsWhenQueueClosed() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        OrderGenerator generator = new OrderGenerator(queue, 50);

        Thread t = new Thread(generator);
        t.start();

        Thread.sleep(80);
        queue.close(); // закрываем очередь — генератор должен завершиться
        t.join(500);

        assertFalse(t.isAlive(), "Генератор должен завершиться когда очередь закрыта");
    }

    @Test
    void stopsWhenStopCalled() throws InterruptedException {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        OrderGenerator generator = new OrderGenerator(queue, 50);

        Thread t = new Thread(generator);
        t.start();

        Thread.sleep(80);
        generator.stop();
        t.interrupt();
        t.join(500);

        assertFalse(t.isAlive());
    }

    @Test
    void throwsOnInvalidInterval() {
        BlockingOrderQueue<Order> queue = new BlockingOrderQueue<>(10);
        assertThrows(IllegalArgumentException.class,
                () -> new OrderGenerator(queue, 0));
    }
}
