package ru.nsu.krasnyanskii.pizzeria;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a single pizza order moving through the pipeline.
 * State changes are logged to stdout as required by the task.
 */
public class Order {

    // Глобальный счётчик — AtomicInteger потокобезопасен без synchronized
    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    public enum State {
        QUEUED("поступил в очередь"),
        COOKING("готовится"),
        COOKED("готова, ожидает склада"),
        IN_STORAGE("на складе"),
        DELIVERING("доставляется"),
        DELIVERED("доставлена клиенту"),
        CANCELLED("отменён (сериализован)");

        private final String description;

        State(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final int id;
    private volatile State state;

    /** Создаёт новый заказ с уникальным id. */
    public Order() {
        this.id = COUNTER.getAndIncrement();
        this.state = State.QUEUED;
    }

    /** Используется при десериализации незавершённых заказов. */
    public Order(int id, State state) {
        this.id = id;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    /**
     * Меняет состояние и сразу печатает: [номер заказа] [состояние]
     * как требует условие задачи.
     */
    public void setState(State state) {
        this.state = state;
        System.out.printf("[%d] %s%n", id, state.getDescription());
    }

    /** Сброс счётчика — нужен только в тестах. */
    static void resetCounter() {
        COUNTER.set(1);
    }
}
