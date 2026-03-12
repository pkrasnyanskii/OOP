package ru.nsu.krasnyanskii.pizzeria.workers;

import lombok.Getter;
import ru.nsu.krasnyanskii.pizzeria.BlockingOrderQueue;
import ru.nsu.krasnyanskii.pizzeria.PizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.Stoppable;
import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;

/** Worker that takes orders from the queue, simulates cooking, and puts pizzas to storage. */
public class Baker implements Runnable, Stoppable {

    @Getter
    private final int id;
    @Getter
    private final int cookingTimeMs;
    private final BlockingOrderQueue<Order> orderQueue;
    private final PizzaStorage storage;
    private final PizzeriaView view;
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
                    break;
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

}
