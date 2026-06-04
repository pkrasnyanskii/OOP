package ru.nsu.krasnyanskii.pizzeria.view;

/** Output contract for events produced by the order generator. */
public interface GeneratorView {

    /** Prints a generator-started message. */
    void generatorStarted();

    /** Prints a generator-stopped message. */
    void generatorStopped();

    /**
     * Prints a new-order message.
     *
     * @param orderId generated order identifier
     */
    void orderGenerated(int orderId);

    /**
     * Prints an order state-change message.
     *
     * @param orderId order identifier
     * @param state   human-readable state description
     */
    void orderStateChanged(int orderId, String state);
}
