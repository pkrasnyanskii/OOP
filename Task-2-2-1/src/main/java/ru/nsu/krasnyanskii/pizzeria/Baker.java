package ru.nsu.krasnyanskii.pizzeria;

/**
 * Пекарь — берёт заказы из очереди, "готовит" (спит cookingTimeMs),
 * затем кладёт на склад.
 *
 * <p>Реализует Runnable — это предпочтительный способ по сравнению
 * с наследованием от Thread, т.к. не тратит наследование.</p>
 */
public class Baker implements Runnable {

    private final int id;
    /** Время приготовления одной пиццы в мс. */
    private final int cookingTimeMs;
    private final BlockingOrderQueue<Order> orderQueue;
    private final PizzaStorage storage;
    private volatile boolean running = true;

    public Baker(int id, int cookingTimeMs,
                 BlockingOrderQueue<Order> orderQueue,
                 PizzaStorage storage) {
        if (cookingTimeMs <= 0) {
            throw new IllegalArgumentException("cookingTimeMs must be positive");
        }
        this.id = id;
        this.cookingTimeMs = cookingTimeMs;
        this.orderQueue = orderQueue;
        this.storage = storage;
    }

    @Override
    public void run() {
        System.out.printf("[Baker-%d] начал работу (время готовки: %d мс)%n", id, cookingTimeMs);
        try {
            while (running) {
                Order order = orderQueue.take();
                if (order == null) {
                    break; // очередь закрыта и пуста
                }

                order.setState(Order.State.COOKING);
                System.out.printf("[Baker-%d] готовит заказ #%d%n", id, order.getId());

                Thread.sleep(cookingTimeMs);

                order.setState(Order.State.COOKED);
                System.out.printf("[Baker-%d] заказ #%d готов, кладёт на склад%n",
                        id, order.getId());

                storage.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.printf("[Baker-%d] завершил работу%n", id);
    }

    /** Мягкая остановка — пекарь завершит текущий заказ. */
    public void stop() {
        running = false;
    }

    public int getId() {
        return id;
    }

    public int getCookingTimeMs() {
        return cookingTimeMs;
    }
}
