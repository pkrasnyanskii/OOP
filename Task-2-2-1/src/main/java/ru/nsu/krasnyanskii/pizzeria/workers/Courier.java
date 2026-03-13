package ru.nsu.krasnyanskii.pizzeria.workers;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.storage.PizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;

/** Worker that picks up batches of pizzas from storage and delivers them. */
public class Courier implements Worker {

    @Getter
    private final int id;
    @Getter
    private final int trunkCapacity;
    private final PizzaStorage storage;
    @Getter
    private final int deliveryTimeMs;
    private final PizzeriaView view;
    private volatile boolean running = true;

    /**
     * Creates a Courier.
     *
     * @param id             courier identifier
     * @param trunkCapacity  max pizzas per trip; must be positive
     * @param deliveryTimeMs delivery duration in ms; must be positive
     * @param storage        shared pizza storage
     * @param view           view for all console output
     */
    public Courier(int id, int trunkCapacity, int deliveryTimeMs,
                   PizzaStorage storage, PizzeriaView view) {
        if (trunkCapacity <= 0) {
            throw new IllegalArgumentException("trunkCapacity must be positive");
        }
        if (deliveryTimeMs <= 0) {
            throw new IllegalArgumentException("deliveryTimeMs must be positive");
        }
        this.id = id;
        this.trunkCapacity = trunkCapacity;
        this.deliveryTimeMs = deliveryTimeMs;
        this.storage = storage;
        this.view = view;
    }

    @Override
    public void run() {
        view.courierStarted(id, trunkCapacity);
        try {
            while (running) {
                List<Order> batch = storage.take(trunkCapacity);
                if (batch.isEmpty()) {
                    break;
                }
                batch.forEach(order -> order.setState(Order.State.DELIVERING));
                String ids = formatIds(batch);
                view.courierDelivering(id, batch.size(), ids);
                Thread.sleep(deliveryTimeMs);
                batch.forEach(order -> order.setState(Order.State.DELIVERED));
                view.courierDelivered(id, ids);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        view.courierFinished(id);
    }

    private String formatIds(List<Order> orders) {
        return orders.stream()
                .map(o -> "#" + o.getId())
                .collect(Collectors.joining(", "));
    }

    @Override
    public void stop() {
        running = false;
    }
}
