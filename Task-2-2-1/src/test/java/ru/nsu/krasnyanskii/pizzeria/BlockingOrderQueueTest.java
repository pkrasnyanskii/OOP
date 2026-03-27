package ru.nsu.krasnyanskii.pizzeria;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class BlockingOrderQueueTest {

    @Test
    void putAndTakeSingleItem() throws InterruptedException {
        BlockingOrderQueue<String> queue = new BlockingOrderQueue<>(5);
        queue.put("pizza");
        assertEquals("pizza", queue.take());
    }

    @Test
    void sizeTracksCorrectly() throws InterruptedException {
        BlockingOrderQueue<String> queue = new BlockingOrderQueue<>(5);
        assertEquals(0, queue.size());
        queue.put("a");
        queue.put("b");
        assertEquals(2, queue.size());
        queue.take();
        assertEquals(1, queue.size());
    }

    @Test
    void takeReturnsNullWhenClosedAndEmpty() throws InterruptedException {
        BlockingOrderQueue<String> queue = new BlockingOrderQueue<>(5);
        queue.close();
        assertNull(queue.take());
    }

    @Test
    void drainAllReturnsAllItems() throws InterruptedException {
        BlockingOrderQueue<String> queue = new BlockingOrderQueue<>(5);
        queue.put("a");
        queue.put("b");
        queue.put("c");
        List<String> drained = queue.drainAll();
        assertEquals(3, drained.size());
        assertEquals(0, queue.size());
    }

    @Test
    void putBlocksWhenFull() throws InterruptedException {
        // Очередь на 1 элемент
        BlockingOrderQueue<String> queue = new BlockingOrderQueue<>(1);
        queue.put("first");

        // Второй put должен заблокироваться
        Thread putter = new Thread(() -> {
            try {
                queue.put("second");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        putter.start();

        // Даём потоку время заблокироваться
        Thread.sleep(100);
        assertTrue(putter.isAlive(), "Поток должен быть заблокирован");

        // Освобождаем место
        queue.take();
        putter.join(500);
        assertFalse(putter.isAlive(), "Поток должен завершиться после освобождения места");
    }

    @Test
    void constructorThrowsOnInvalidCapacity() {
        assertThrows(IllegalArgumentException.class, () -> new BlockingOrderQueue<>(0));
        assertThrows(IllegalArgumentException.class, () -> new BlockingOrderQueue<>(-1));
    }
}
