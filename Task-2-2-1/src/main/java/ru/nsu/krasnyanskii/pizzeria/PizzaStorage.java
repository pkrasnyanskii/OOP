package ru.nsu.krasnyanskii.pizzeria;


/**
 * Thread-safe warehouse for finished pizzas.
 * Capacity-limited; bakers block when full, couriers block when empty.
 * Uses intrinsic locks only — no java.util.concurrent collections.
 */
public class PizzaStorage {
    private final int capacity;
    private final LinkedList<Order> orders = new LinkedList<>();
    private volatile boolean accepting = true;

    public PizzaStorage(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Baker puts a pizza on the shelf.
     * Blocks if storage is full.
     */
    public synchronized void put(Order order) throws InterruptedException {
        while (orders.size() >= capacity) {
            wait();
        }
        orders.addLast(order);
        order.setState(Order.State.IN_STORAGE);
        notifyAll();
    }

    /**
     * Courier takes up to {@code maxCount} pizzas.
     * Blocks if storage is empty (and still accepting new pizzas).
     * Returns empty list only when storage is closed and empty.
     */
    public synchronized List<Order> take(int maxCount) throws InterruptedException {
        while (orders.isEmpty() && accepting) {
            wait();
        }
        List<Order> taken = new ArrayList<>();
        int count = Math.min(maxCount, orders.size());
        for (int i = 0; i < count; i++) {
            taken.add(orders.removeFirst());
        }
        notifyAll();
        return taken;
    }

    public synchronized boolean isEmpty() {
        return orders.isEmpty();
    }

    public synchronized int size() {
        return orders.size();
    }

    /** Stop accepting new pizzas; wake up any waiting couriers. */
    public synchronized void closeAccepting() {
        accepting = false;
        notifyAll();
    }

    public boolean isAccepting() {
        return accepting;
    }

    /** Drain all remaining pizzas (for serialization). */
    public synchronized List<Order> drainAll() {
        List<Order> result = new ArrayList<>(orders);
        orders.clear();
        notifyAll();
        return result;
    }
}
