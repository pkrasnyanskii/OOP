package ru.nsu.krasnyanskii.pizzeria.view;

/** Output contract for events produced by bakers. */
public interface BakerView {

    /**
     * Prints a baker-started message.
     *
     * @param id            baker identifier
     * @param cookingTimeMs cooking time per pizza in ms
     */
    void bakerStarted(int id, int cookingTimeMs);

    /**
     * Prints a baker-cooking message.
     *
     * @param bakerId baker identifier
     * @param orderId order being cooked
     */
    void bakerCooking(int bakerId, int orderId);

    /**
     * Prints a baker-cooked message.
     *
     * @param bakerId baker identifier
     * @param orderId order that finished cooking
     */
    void bakerCooked(int bakerId, int orderId);

    /**
     * Prints a baker-finished message.
     *
     * @param id baker identifier
     */
    void bakerFinished(int id);

    /**
     * Prints an order state-change message.
     *
     * @param orderId order identifier
     * @param state   human-readable state description
     */
    void orderStateChanged(int orderId, String state);
}
