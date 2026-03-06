package ru.nsu.krasnyanskii.pizzeria;

/**
 * Baker picks orders from the queue, "cooks" them (sleep proportional to speed),
 * then places them into storage.
 */
public class Baker implements Runnable {
    private final int id;
    /** Cooking speed in ms per pizza (lower = faster). */
    private final int cookingTimeMs;
    private final BlockingOrderQueue<Order> orderQueue;
    private final PizzaStorage storage;
    private volatile boolean running = true;

    public Baker(int id, int cookingTimeMs,
                 BlockingOrderQueue<Order> orderQueue,
                 PizzaStorage storage) {
        this.id = id;
        this.cookingTimeMs = cookingTimeMs;
        this.orderQueue = orderQueue;
        this.storage = storage;
    }

    @Override
    public void run() {
        System.out.printf("[Baker-%d] начал работу (скорость готовки: %d мс)%n", id, cookingTimeMs);
        try {
            while (running) {
                Order order = orderQueue.take();
                if (order == null) break;   // queue closed and empty

                order.setState(Order.State.COOKING);
                System.out.printf("[Baker-%d] готовит заказ #%d%n", id, order.getId());

                Thread.sleep(cookingTimeMs);

                order.setState(Order.State.COOKED);
                System.out.printf("[Baker-%d] заказ #%d готов, кладёт на склад%n", id, order.getId());

                storage.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.printf("[Baker-%d] завершил работу%n", id);
    }

    public void stop() {
        running = false;
    }

    public int getId() { return id; }
    public int getCookingTimeMs() { return cookingTimeMs; }
}
