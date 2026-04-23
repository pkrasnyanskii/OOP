package ru.nsu.krasnyanskii.pizzeria;

import java.util.List;

/**
 * View layer for the pizzeria simulation.
 *
 * <p><b>SRP</b>: this class is the single place responsible for all console output.
 * No other class calls {@code System.out} directly.</p>
 */
public class PizzeriaView {

    // --- Pizzeria lifecycle ---

    public void pizzeriaOpened() {
        System.out.println("=== Pizzeria is opening! ===");
    }

    public void pizzeriaClosed() {
        System.out.println("=== Pizzeria is closed. Goodbye! ===");
    }

    public void allOrdersDone() {
        System.out.println("[Pizzeria] All orders completed!");
    }

    public void workingFor(long ms) {
        System.out.printf("[Pizzeria] Working for %d ms...%n", ms);
    }

    public void shutdownStarted() {
        System.out.println("\n=== Time is up! Starting shutdown... ===");
    }

    // --- Baker ---

    public void bakerStarted(int id, int cookingTimeMs) {
        System.out.printf("[Baker-%d] started (cooking time: %d ms)%n", id, cookingTimeMs);
    }

    public void bakerCooking(int bakerId, int orderId) {
        System.out.printf("[Baker-%d] cooking order #%d%n", bakerId, orderId);
    }

    public void bakerCooked(int bakerId, int orderId) {
        System.out.printf("[Baker-%d] order #%d ready, putting to storage%n", bakerId, orderId);
    }

    public void bakerFinished(int id) {
        System.out.printf("[Baker-%d] finished%n", id);
    }

    // --- Courier ---

    public void courierStarted(int id, int trunkCapacity) {
        System.out.printf("[Courier-%d] started (trunk: %d pizzas)%n", id, trunkCapacity);
    }

    public void courierDelivering(int courierId, int count, String ids) {
        System.out.printf("[Courier-%d] delivering %d pizzas: %s%n", courierId, count, ids);
    }

    public void courierDelivered(int courierId, String ids) {
        System.out.printf("[Courier-%d] delivered: %s%n", courierId, ids);
    }

    public void courierFinished(int id) {
        System.out.printf("[Courier-%d] finished%n", id);
    }

    // --- OrderGenerator ---

    public void generatorStarted() {
        System.out.println("[OrderGenerator] started accepting orders");
    }

    public void generatorStopped() {
        System.out.println("[OrderGenerator] stopped");
    }

    public void orderGenerated(int orderId) {
        System.out.printf("[OrderGenerator] new order #%d%n", orderId);
    }

    // --- Order state ---

    public void orderStateChanged(int orderId, String state) {
        System.out.printf("[#%d] %s%n", orderId, state);
    }

    // --- Serializer ---

    public void serializerSaved(int count, String filePath) {
        System.out.printf("[Serializer] Saved %d unfinished orders to %s%n", count, filePath);
    }
}
