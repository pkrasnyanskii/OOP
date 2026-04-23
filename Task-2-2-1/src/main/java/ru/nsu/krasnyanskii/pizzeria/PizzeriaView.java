package ru.nsu.krasnyanskii.pizzeria;

/**
 * View layer for the pizzeria simulation.
 *
 * <p><b>SRP</b>: this class is the single place responsible for all console output.
 * No other class calls {@code System.out} directly.</p>
 */
public class PizzeriaView {

    /** Creates a PizzeriaView. */
    public PizzeriaView() {
    }

    // --- Pizzeria lifecycle ---

    /** Prints the pizzeria-opened banner. */
    public void pizzeriaOpened() {
        System.out.println("=== Pizzeria is opening! ===");
    }

    /** Prints the pizzeria-closed banner. */
    public void pizzeriaClosed() {
        System.out.println("=== Pizzeria is closed. Goodbye! ===");
    }

    /** Prints a confirmation that all orders were completed. */
    public void allOrdersDone() {
        System.out.println("[Pizzeria] All orders completed!");
    }

    /**
     * Prints how long the pizzeria will run.
     *
     * @param ms working duration in milliseconds
     */
    public void workingFor(long ms) {
        System.out.printf("[Pizzeria] Working for %d ms...%n", ms);
    }

    /** Prints the shutdown-started banner. */
    public void shutdownStarted() {
        System.out.println("\n=== Time is up! Starting shutdown... ===");
    }

    // --- Baker ---

    /**
     * Prints a baker-started message.
     *
     * @param id            baker identifier
     * @param cookingTimeMs cooking time per pizza in ms
     */
    public void bakerStarted(int id, int cookingTimeMs) {
        System.out.printf("[Baker-%d] started (cooking time: %d ms)%n", id, cookingTimeMs);
    }

    /**
     * Prints a baker-cooking message.
     *
     * @param bakerId baker identifier
     * @param orderId order being cooked
     */
    public void bakerCooking(int bakerId, int orderId) {
        System.out.printf("[Baker-%d] cooking order #%d%n", bakerId, orderId);
    }

    /**
     * Prints a baker-cooked message.
     *
     * @param bakerId baker identifier
     * @param orderId order that finished cooking
     */
    public void bakerCooked(int bakerId, int orderId) {
        System.out.printf("[Baker-%d] order #%d ready, putting to storage%n", bakerId, orderId);
    }

    /**
     * Prints a baker-finished message.
     *
     * @param id baker identifier
     */
    public void bakerFinished(int id) {
        System.out.printf("[Baker-%d] finished%n", id);
    }

    // --- Courier ---

    /**
     * Prints a courier-started message.
     *
     * @param id            courier identifier
     * @param trunkCapacity max pizzas per trip
     */
    public void courierStarted(int id, int trunkCapacity) {
        System.out.printf("[Courier-%d] started (trunk: %d pizzas)%n", id, trunkCapacity);
    }

    /**
     * Prints a courier-delivering message.
     *
     * @param courierId courier identifier
     * @param count     number of pizzas in this trip
     * @param ids       formatted order id list
     */
    public void courierDelivering(int courierId, int count, String ids) {
        System.out.printf("[Courier-%d] delivering %d pizzas: %s%n", courierId, count, ids);
    }

    /**
     * Prints a courier-delivered message.
     *
     * @param courierId courier identifier
     * @param ids       formatted order id list
     */
    public void courierDelivered(int courierId, String ids) {
        System.out.printf("[Courier-%d] delivered: %s%n", courierId, ids);
    }

    /**
     * Prints a courier-finished message.
     *
     * @param id courier identifier
     */
    public void courierFinished(int id) {
        System.out.printf("[Courier-%d] finished%n", id);
    }

    // --- OrderGenerator ---

    /** Prints a generator-started message. */
    public void generatorStarted() {
        System.out.println("[OrderGenerator] started accepting orders");
    }

    /** Prints a generator-stopped message. */
    public void generatorStopped() {
        System.out.println("[OrderGenerator] stopped");
    }

    /**
     * Prints a new-order message.
     *
     * @param orderId generated order identifier
     */
    public void orderGenerated(int orderId) {
        System.out.printf("[OrderGenerator] new order #%d%n", orderId);
    }

    // --- Order state ---

    /**
     * Prints an order state-change message.
     *
     * @param orderId order identifier
     * @param state   human-readable state description
     */
    public void orderStateChanged(int orderId, String state) {
        System.out.printf("[#%d] %s%n", orderId, state);
    }

    // --- Serializer ---

    /**
     * Prints a serializer-saved confirmation.
     *
     * @param count    number of saved orders
     * @param filePath destination file path
     */
    public void serializerSaved(int count, String filePath) {
        System.out.printf("[Serializer] Saved %d unfinished orders to %s%n", count, filePath);
    }
}
