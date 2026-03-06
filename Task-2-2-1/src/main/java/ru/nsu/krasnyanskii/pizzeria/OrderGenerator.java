package ru.nsu.krasnyanskii.pizzeria;


/**
 * Generates orders at a fixed interval until told to stop.
 */
public class OrderGenerator implements Runnable {
    private final BlockingOrderQueue<Order> orderQueue;
    private final int intervalMs;
    private volatile boolean running = true;

    public OrderGenerator(BlockingOrderQueue<Order> orderQueue, int intervalMs) {
        this.orderQueue = orderQueue;
        this.intervalMs = intervalMs;
    }

    @Override
    public void run() {
        System.out.println("[OrderGenerator] начал принимать заказы");
        try {
            while (running && !orderQueue.isClosed()) {
                Thread.sleep(intervalMs);
                if (!running || orderQueue.isClosed()) break;
                Order order = new Order();
                order.setState(Order.State.QUEUED);
                orderQueue.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("[OrderGenerator] приём заказов остановлен");
    }

    public void stop() {
        running = false;
    }
}
