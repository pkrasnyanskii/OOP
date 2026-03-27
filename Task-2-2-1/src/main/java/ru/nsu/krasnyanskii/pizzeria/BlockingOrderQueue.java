package ru.nsu.krasnyanskii.pizzeria;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Custom bounded blocking queue using intrinsic locks (wait / notifyAll).
 *
 * <p>Запрещено использовать java.util.concurrent.BlockingQueue — поэтому
 * реализуем блокировку вручную через synchronized + wait/notifyAll.</p>
 *
 * <p>Принцип работы:
 *   put() — если очередь полна, поток ждёт (wait) пока не освободится место
 *   take() — если очередь пуста, поток ждёт (wait) пока не появится элемент
 *   close() — сигнализирует всем ждущим потокам что очередь закрыта
 * </p>
 */
public class BlockingOrderQueue<T> {

    private final LinkedList<T> queue = new LinkedList<>();
    private final int capacity;
    private volatile boolean closed = false;

    /**
     * Creates a new bounded blocking queue.
     *
     * @param capacity maximum number of elements; must be positive
     * @throws IllegalArgumentException if capacity is not positive
     */
    public BlockingOrderQueue(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Добавляет элемент. Блокирует если очередь полна.
     */
    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() >= capacity && !closed) {
            wait(); // освобождаем монитор и ждём notifyAll
        }
        if (closed) {
            return;
        }
        queue.addLast(item);
        notifyAll(); // будим потоки которые ждут в take()
    }

    /**
     * Забирает элемент. Блокирует если очередь пуста и не закрыта.
     *
     * @return элемент или null если очередь закрыта и пуста
     */
    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty() && !closed) {
            wait();
        }
        if (queue.isEmpty()) {
            return null; // закрыта и пуста — сигнал для пекаря завершить работу
        }
        T item = queue.removeFirst();
        notifyAll(); // будим потоки которые ждут в put()
        return item;
    }

    /**
     * Закрывает очередь — все ждущие потоки получат null из take().
     */
    public synchronized void close() {
        closed = true;
        notifyAll();
    }

    /**
     * Забирает все оставшиеся элементы (для сериализации при остановке).
     */
    public synchronized List<T> drainAll() {
        List<T> result = new ArrayList<>(queue);
        queue.clear();
        notifyAll();
        return result;
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    public synchronized int size() {
        return queue.size();
    }

    public boolean isClosed() {
        return closed;
    }
}