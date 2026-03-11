package ru.nsu.krasnyanskii.pizzeria.workers;

import ru.nsu.krasnyanskii.pizzeria.BlockingOrderQueue;
import ru.nsu.krasnyanskii.pizzeria.Stoppable;
import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;

/** Produces new orders at a fixed interval until stopped. */
public class OrderGenerator implements Runnable, Stoppable {

    private final BlockingOrderQueue<Order> orderQueue;
    private final int intervalMs;
    private final PizzeriaView view;
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
