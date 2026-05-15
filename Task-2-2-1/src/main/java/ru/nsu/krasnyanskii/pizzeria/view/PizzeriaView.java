package ru.nsu.krasnyanskii.pizzeria.view;

/** Output contract for the pizzeria simulation. */
public interface PizzeriaView {

    /** Prints the pizzeria-opened banner. */
    void pizzeriaOpened();

    /** Prints the pizzeria-closed banner. */
    void pizzeriaClosed();

    /** Prints a confirmation that all orders were completed. */
    void allOrdersDone();

    /**
     * Prints how long the pizzeria will run.
     *
     * @param ms working duration in milliseconds
     */
    void workingFor(long ms);

    /** Prints the shutdown-started banner. */
    void shutdownStarted();

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
     * Prints a courier-started message.
     *
     * @param id            courier identifier
     * @param trunkCapacity max pizzas per trip
     */
    void courierStarted(int id, int trunkCapacity);

    /**
     * Prints a courier-delivering message.
     *
     * @param courierId courier identifier
     * @param count     number of pizzas in this trip
     * @param ids       formatted order id list
     */
    void courierDelivering(int courierId, int count, String ids);

    /**
     * Prints a courier-delivered message.
     *
     * @param courierId courier identifier
     * @param ids       formatted order id list
     */
    void courierDelivered(int courierId, String ids);

    /**
     * Prints a courier-finished message.
     *
     * @param id courier identifier
     */
    void courierFinished(int id);

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

    /**
     * Prints a serializer-saved confirmation.
     *
     * @param count    number of saved orders
     * @param filePath destination file path
     */
    void serializerSaved(int count, String filePath);
}
