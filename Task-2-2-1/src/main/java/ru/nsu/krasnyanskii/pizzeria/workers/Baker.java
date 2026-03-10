package ru.nsu.krasnyanskii.pizzeria.workers;

import ru.nsu.krasnyanskii.pizzeria.BlockingOrderQueue;
import ru.nsu.krasnyanskii.pizzeria.Order;
import ru.nsu.krasnyanskii.pizzeria.PizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.PizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.Stoppable;

/**
 * Worker that takes orders from the queue, simulates cooking, and puts pizzas to storage.
 *
 * <p>Implements {@link Stoppable} (ISP): the interface exposes only {@code stop()},
 * letting {@code Pizzeria} manage all workers through a single abstraction (DIP).</p>
 */
public class Baker implements Runnable, Stoppable {

    private final int id;
    private final int cookingTimeMs;
    private final BlockingOrderQueue<Order> orderQueue;
    private final PizzaStorage storage;
    private final PizzeriaView view;

    /**
     * {@code volatile} ensures that a write in one thread is immediately visible to all
     * other threads without CPU-register caching. Without it, the baker thread might
     * never observe the {@code false} written by {@code stop()}.
     */
    private volatile boolean running = true;

    /**
     * Creates a Baker.
     *
     * @param id            baker identifier
     * @param cookingTimeMs cooking duration per pizza in ms; must be positive
     * @param orderQueue    shared order queue
     * @param storage       shared pizza storage
     * @param view          view for all console output
     */
    public Baker(int id, int cookingTimeMs,
                 BlockingOrderQueue<Order> orderQueue,
                 PizzaStorage storage,
                 PizzeriaView view) {
        if (cookingTimeMs <= 0) {
            throw new IllegalArgumentException("cookingTimeMs must be positive");
        }
        this.id = id;
        this.cookingTimeMs = cookingTimeMs;
        this.orderQueue = orderQueue;
        this.storage = storage;
        this.view = view;
    }

    @Override
    public void run() {
        view.bakerStarted(id, cookingTimeMs);
        try {
            while (running) {
                Order order = orderQueue.take();
                if (order == null) {
                    break; // queue closed and empty
                }
                order.setState(Order.State.COOKING);
                view.bakerCooking(id, order.getId());
                Thread.sleep(cookingTimeMs);
                order.setState(Order.State.COOKED);
                view.bakerCooked(id, order.getId());
                storage.put(order);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        view.bakerFinished(id);
    }

    @Override
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
