package ru.nsu.krasnyanskii.pizzeria;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Thread-safe bounded storage for finished pizzas.
 *
 * <p>Пекари кладут пиццы, курьеры забирают партиями.
 * Реализация через intrinsic lock (synchronized + wait/notifyAll),
 * без java.util.concurrent коллекций.</p>
 */
public class PizzaStorage {

    private final int capacity;
    private final LinkedList<Order> orders = new LinkedList<>();
    // false = больше пицц не будет (пиццерия закрывается)
    private volatile boolean accepting = true;

    public PizzaStorage(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Storage capacity must be positive");
        }
        this.capacity = capacity;
    }

    /**
     * Пекарь кладёт пиццу на склад.
     * Блокирует если склад полон — ждёт пока курьер не заберёт партию.
     */
    public synchronized void put(Order order) throws InterruptedException {
        while (orders.size() >= capacity) {
            wait();
        }
        orders.addLast(order);
        order.setState(Order.State.IN_STORAGE);
        notifyAll(); // будим курьеров которые ждут в take()
    }

    /**
     * Курьер забирает до maxCount пицц.
     * Блокирует если склад пуст и ещё принимает новые заказы.
     *
     * @return список пицц или пустой список если склад закрыт и пуст
     */
    public synchronized List<Order> take(int maxCount) throws InterruptedException {
        while (orders.isEmpty() && accepting) {
            wait(); // ждём пока пекарь что-нибудь положит
        }
        List<Order> taken = new ArrayList<>();
        int count = Math.min(maxCount, orders.size());
        for (int i = 0; i < count; i++) {
            taken.add(orders.removeFirst());
        }
        notifyAll(); // будим пекарей которые ждут места
        return taken;
    }

    /**
     * Останавливает приём новых пицц.
     * Курьеры заберут оставшееся и завершат работу.
     */
    public synchronized void closeAccepting() {
        accepting = false;
        notifyAll();
    }

    /**
     * Забирает все пиццы (для сериализации).
     */
    public synchronized List<Order> drainAll() {
        List<Order> result = new ArrayList<>(orders);
        orders.clear();
        notifyAll();
        return result;
    }

    public synchronized boolean isEmpty() {
        return orders.isEmpty();
    }

    public synchronized int size() {
        return orders.size();
    }

    public boolean isAccepting() {
        return accepting;
    }
}
