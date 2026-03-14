package ru.nsu.krasnyanskii.pizzeria.workers;

import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.queue.OrderQueue;
import ru.nsu.krasnyanskii.pizzeria.view.GeneratorView;

/** Produces new orders at a fixed interval until interrupted or the queue is closed. */
public class OrderGenerator implements Worker {

    private final OrderQueue<Order> orderQueue;
    private final int intervalMs;
    private final GeneratorView view;

    /**
     * Creates an OrderGenerator.
     *
     * @param orderQueue queue to put new orders into
     * @param intervalMs interval between orders in ms; must be positive
     * @param view       view for all console output
     */
    public OrderGenerator(OrderQueue<Order> orderQueue,
                          int intervalMs,
                          GeneratorView view) {
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
            while (!Thread.currentThread().isInterrupted() && !orderQueue.isClosed()) {
                Thread.sleep(intervalMs);
                if (orderQueue.isClosed()) {
                    break;
                }
                Order order = new Order();
                view.orderStateChanged(order.getId(), Order.State.QUEUED.getDescription());
                view.orderGenerated(order.getId());
                orderQueue.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        view.generatorStopped();
    }
}
