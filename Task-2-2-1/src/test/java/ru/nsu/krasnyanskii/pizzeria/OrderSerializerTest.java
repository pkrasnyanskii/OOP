package ru.nsu.krasnyanskii.pizzeria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderSerializerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void resetCounter() {
        Order.resetCounter();
    }

    @Test
    void saveAndLoad() throws IOException {
        Order o1 = new Order(42, Order.State.QUEUED);
        Order o2 = new Order(99, Order.State.COOKING);

        String path = tempDir.resolve("orders.json").toString();
        OrderSerializer.save(List.of(o1, o2), path);

        List<Order> loaded = OrderSerializer.load(path);
        assertEquals(2, loaded.size());
        assertEquals(42, loaded.get(0).getId());
        assertEquals(Order.State.QUEUED, loaded.get(0).getState());
        assertEquals(99, loaded.get(1).getId());
        assertEquals(Order.State.COOKING, loaded.get(1).getState());
    }

    @Test
    void saveEmptyList() throws IOException {
        String path = tempDir.resolve("empty.json").toString();
        OrderSerializer.save(List.of(), path);
        List<Order> loaded = OrderSerializer.load(path);
        assertTrue(loaded.isEmpty());
    }
}
