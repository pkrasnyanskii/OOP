package ru.nsu.krasnyanskii.pizzeria;

import java.util.concurrent.atomic.AtomicInteger;

public class Order {
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
        State(String description) { this.description = description; }
        public String getDescription() { return description; }
    }

    private final int id;
    private volatile State state;

    public Order() {
        this.id = COUNTER.getAndIncrement();
        this.state = State.QUEUED;
    }

    // For deserialization
    public Order(int id, State state) {
        this.id = id;
        this.state = state;
    }

    public int getId() { return id; }
    public State getState() { return state; }

    public void setState(State state) {
        this.state = state;
        System.out.printf("[%d] %s%n", id, state.getDescription());
    }
}
