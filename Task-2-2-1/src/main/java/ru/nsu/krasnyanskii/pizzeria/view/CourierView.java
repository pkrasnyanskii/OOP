package ru.nsu.krasnyanskii.pizzeria.view;

/** Output contract for events produced by couriers. */
public interface CourierView {

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
}
