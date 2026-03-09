package ru.nsu.krasnyanskii.pizzeria;

import java.util.List;

/**
 * Курьер — забирает партию пицц со склада (до trunkCapacity штук)
 * и "доставляет" (спит deliveryTimeMs).
 */
public class Courier implements Runnable {

    private final int id;
    /** Максимальное количество пицц за одну поездку. */
    private final int trunkCapacity;
    private final PizzaStorage storage;
    /** Время одной доставки в мс. */
    private final int deliveryTimeMs;
    private volatile boolean running = true;

    /**
     * Creates a courier.
     *
     * @param id            courier identifier
     * @param trunkCapacity max pizzas per trip; must be positive
     * @param deliveryTimeMs delivery duration in ms; must be positive
     * @param storage       shared pizza storage
     */
    public Courier(int id, int trunkCapacity, int deliveryTimeMs, PizzaStorage storage) {
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
    }

    @Override
    public void run() {
        System.out.printf("[Courier-%d] начал работу (багажник: %d пицц)%n",
                id, trunkCapacity);
        try {
            while (running) {
                List<Order> batch = storage.take(trunkCapacity);
                if (batch.isEmpty()) {
                    break; // склад закрыт и пуст
                }

                for (Order order : batch) {
                    order.setState(Order.State.DELIVERING);
                }
                System.out.printf("[Courier-%d] везёт %d пицц: %s%n",
                        id, batch.size(), formatIds(batch));

                Thread.sleep(deliveryTimeMs);

                for (Order order : batch) {
                    order.setState(Order.State.DELIVERED);
                }
                System.out.printf("[Courier-%d] доставил: %s%n", id, formatIds(batch));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.printf("[Courier-%d] завершил работу%n", id);
    }

    private String formatIds(List<Order> orders) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < orders.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("#").append(orders.get(i).getId());
        }
        return sb.toString();
    }

    public void stop() {
        running = false;
    }

    public int getId() {
        return id;
    }

    public int getTrunkCapacity() {
        return trunkCapacity;
    }

    public int getDeliveryTimeMs() {
        return deliveryTimeMs;
    }
}