package ru.nsu.krasnyanskii.pizzeria;

import java.util.concurrent.atomic.AtomicInteger;

/** Represents a single pizza order moving through the pipeline. */
public class Order {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    /** Lifecycle states of an order. */
    public enum State {
        QUEUED("queued"),
        COOKING("cooking"),
        COOKED("cooked, waiting for storage"),
        IN_STORAGE("in storage"),
        DELIVERING("delivering"),
        DELIVERED("delivered"),
        CANCELLED("cancelled (serialized)");

        private final String description;

        State(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    private final int id;

    /**
     * {@code volatile} ensures state changes written by one thread (baker/courier)
     * are immediately visible to all other threads without CPU-register caching.
     */
    private volatile State state;

    private final PizzeriaView view;

    /**
     * Creates a new order and logs the QUEUED state via view.
     *
     * @param view view for state-change output
     */
    public Order(PizzeriaView view) {
        this.id = COUNTER.getAndIncrement();
        this.state = State.QUEUED;
        this.view = view;
        view.orderStateChanged(id, State.QUEUED.getDescription());
    }

    /**
     * Creates a new order without view (no logging; used in tests).
     */
    public Order() {
        this.id = COUNTER.getAndIncrement();
        this.state = State.QUEUED;
        this.view = null;
    }

    /**
     * Deserialization constructor — restores a saved order, no logging.
     *
     * @param id    saved order id
     * @param state saved state
     */
    public Order(int id, State state) {
        this.id = id;
        this.state = state;
        this.view = null;
    }

    public int getId() {
        return id;
    }

    public State getState() {
        return state;
    }

    /** Updates the state and notifies the view if one is present. */
    public void setState(State state) {
        this.state = state;
        if (view != null) {
            view.orderStateChanged(id, state.getDescription());
        }
    }

    /** Resets the id counter; for tests only. */
    static void resetCounter() {
        COUNTER.set(1);
    }
}
