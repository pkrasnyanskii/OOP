package ru.nsu.krasnyanskii.pizzeria.model;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/** Represents a single pizza order moving through the pipeline. */
@Getter
public class Order {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    /** Lifecycle states of an order. */
    @Getter
    @RequiredArgsConstructor
    public enum State {
        /** Order placed and waiting in queue. */
        QUEUED("queued"),
        /** Order is being cooked. */
        COOKING("cooking"),
        /** Order cooked, waiting for storage space. */
        COOKED("cooked, waiting for storage"),
        /** Order sitting in storage, awaiting courier. */
        IN_STORAGE("in storage"),
        /** Order picked up and on the way. */
        DELIVERING("delivering"),
        /** Order successfully delivered. */
        DELIVERED("delivered"),
        /** Order cancelled and serialized on shutdown. */
        CANCELLED("cancelled (serialized)");

        /** Human-readable description of this state. */
        private final String description;
    }

    /** Unique order identifier. */
    private final int id;

    /** Current lifecycle state. */
    @Setter
    private volatile State state;

    /** Creates a new order in the QUEUED state. */
    public Order() {
        this.id = COUNTER.getAndIncrement();
        this.state = State.QUEUED;
    }

    /**
     * Deserialization constructor — restores a saved order without auto-incrementing the counter.
     *
     * @param id    saved order id
     * @param state saved state
     */
    public Order(int id, State state) {
        this.id = id;
        this.state = state;
    }

    /** Resets the id counter; for tests only. */
    public static void resetCounter() {
        COUNTER.set(1);
    }
}
