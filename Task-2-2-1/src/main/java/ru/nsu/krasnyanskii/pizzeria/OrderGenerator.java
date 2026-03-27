package ru.nsu.krasnyanskii.pizzeria;

/**
 * Генерирует заказы с фиксированным интервалом пока не будет остановлен.
 */
public class OrderGenerator implements Runnable {

    private final BlockingOrderQueue<Order> orderQueue;
    private final int intervalMs;
    private volatile boolean running = true;

    /**
     * Creates an order generator.
     *
     * @param orderQueue queue to put new orders into
     * @param intervalMs interval between orders in ms; must be positive
     * @throws IllegalArgumentException if intervalMs is not positive
     */
    public OrderGenerator(BlockingOrderQueue<Order> orderQueue, int intervalMs) {
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("intervalMs must be positive");
        }
        this.orderQueue = orderQueue;
        this.intervalMs = intervalMs;
    }

    @Override
    public void run() {
        System.out.println("[OrderGenerator] начал принимать заказы");
        try {
            while (running && !orderQueue.isClosed()) {
                Thread.sleep(intervalMs);
                if (!running || orderQueue.isClosed()) {
                    break;
                }
                Order order = new Order();
                System.out.printf("[OrderGenerator] новый заказ #%d%n", order.getId());
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