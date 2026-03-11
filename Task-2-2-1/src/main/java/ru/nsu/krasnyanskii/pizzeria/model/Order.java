package ru.nsu.krasnyanskii.pizzeria.model;

import java.util.concurrent.atomic.AtomicInteger;

import lombok.Getter;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;

/** Represents a single pizza order moving through the pipeline. */
public class Order {

    private static final AtomicInteger COUNTER = new AtomicInteger(1);

    /** Lifecycle states of an order. */
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

        private final String description;

        State(String description) {
            this.description = description;
        }

        /**
         * Returns the human-readable description of this state.
         *
         * @return state description
         */
        public String getDescription() {
            return description;
        }
    }

    /**
     * -- GETTER --
     *  Returns the unique order identifier.
     *
     * @return order id
     */
    @Getter
    private final int id;
    /**
     * -- GETTER --
     *  Returns the current lifecycle state.
     *
     * @return current state
     */
    @Getter
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

    /** Creates a new order without view; no logging (used in tests). */
    public Order() {
        this.id = COUNTER.getAndIncrement();
        this.state = State.QUEUED;
        this.view = null;
    }

    /**
     * Deserialization constructor — restores a saved order without logging.
     *
     * @param id    saved order id
     * @param state saved state
     */
    public Order(int id, State state) {
        this.id = id;
        this.state = state;
        this.view = null;
    }

    /**
     * Updates the state and notifies the view if one is present.
     *
     * @param state new state
     */
    public void setState(State state) {
        this.state = state;
        if (view != null) {
            view.orderStateChanged(id, state.getDescription());
        }
    }

    /** Resets the id counter; for tests only. */
    public static void resetCounter() {
        COUNTER.set(1);
    }
}
