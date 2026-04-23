package ru.nsu.krasnyanskii.pizzeria.workers;

import ru.nsu.krasnyanskii.pizzeria.BlockingOrderQueue;
import ru.nsu.krasnyanskii.pizzeria.Order;
import ru.nsu.krasnyanskii.pizzeria.PizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.Stoppable;

/**
 * Produces new orders at a fixed interval until stopped.
 *
 * <p><b>ISP</b> (Interface Segregation): implements only {@link Stoppable}, a focused
 * single-method interface. A "fat" interface would force unrelated methods on every
 * worker class.</p>
 *
 * <p><b>DIP</b> (Dependency Inversion): {@code Pizzeria} (high-level module) depends on
 * the {@link Stoppable} abstraction, not on concrete classes. Both the high-level and
 * low-level modules depend on the abstraction, not on each other.</p>
 */
public class OrderGenerator implements Runnable, Stoppable {

    private final BlockingOrderQueue<Order> orderQueue;
    private final int intervalMs;
    private final PizzeriaView view;

    /**
     * {@code volatile} ensures the {@code false} written by {@code stop()} is
     * visible to the generator thread without additional synchronization.
     */
    private volatile boolean running = true;

    /**
     * Creates an OrderGenerator.
     *
     * @param orderQueue queue to put new orders into
     * @param intervalMs interval between orders in ms; must be positive
     * @param view       view for all console output
     */
    public OrderGenerator(BlockingOrderQueue<Order> orderQueue,
                          int intervalMs,
                          PizzeriaView view) {
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("intervalMs must be positive");
        }
        this.orderQueue = orderQueue;
        this.intervalMs = intervalMs;
        this.view = view;
    }

    @Override
    public void run() {
        view.generatorStarted();
        try {
            while (running && !orderQueue.isClosed()) {
                Thread.sleep(intervalMs);
                if (!running || orderQueue.isClosed()) {
                    break;
                }
                Order order = new Order(view);
                view.orderGenerated(order.getId());
                orderQueue.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        view.generatorStopped();
    }

    @Override
    public void stop() {
        running = false;
    }
}
