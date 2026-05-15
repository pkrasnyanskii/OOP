package ru.nsu.krasnyanskii.pizzeria.view;

/** Console implementation of {@link PizzeriaView} — prints all output to stdout. */
public class ConsolePizzeriaView implements PizzeriaView {

    /** Creates a ConsolePizzeriaView. */
    public ConsolePizzeriaView() {
    }

    @Override
    public void pizzeriaOpened() {
        System.out.println("=== Pizzeria is opening! ===");
    }

    @Override
    public void pizzeriaClosed() {
        System.out.println("=== Pizzeria is closed. Goodbye! ===");
    }

    @Override
    public void allOrdersDone() {
        System.out.println("[Pizzeria] All orders completed!");
    }

    @Override
    public void workingFor(long ms) {
        System.out.printf("[Pizzeria] Working for %d ms...%n", ms);
    }

    @Override
    public void shutdownStarted() {
        System.out.println("\n=== Time is up! Starting shutdown... ===");
    }

    @Override
    public void bakerStarted(int id, int cookingTimeMs) {
        System.out.printf("[Baker-%d] started (cooking time: %d ms)%n", id, cookingTimeMs);
    }

    @Override
    public void bakerCooking(int bakerId, int orderId) {
        System.out.printf("[Baker-%d] cooking order #%d%n", bakerId, orderId);
    }

    @Override
    public void bakerCooked(int bakerId, int orderId) {
        System.out.printf("[Baker-%d] order #%d ready, putting to storage%n", bakerId, orderId);
    }

    @Override
    public void bakerFinished(int id) {
        System.out.printf("[Baker-%d] finished%n", id);
    }

    @Override
    public void courierStarted(int id, int trunkCapacity) {
        System.out.printf("[Courier-%d] started (trunk: %d pizzas)%n", id, trunkCapacity);
    }

    @Override
    public void courierDelivering(int courierId, int count, String ids) {
        System.out.printf("[Courier-%d] delivering %d pizzas: %s%n", courierId, count, ids);
    }

    @Override
    public void courierDelivered(int courierId, String ids) {
        System.out.printf("[Courier-%d] delivered: %s%n", courierId, ids);
    }

    @Override
    public void courierFinished(int id) {
        System.out.printf("[Courier-%d] finished%n", id);
    }

    @Override
    public void generatorStarted() {
        System.out.println("[OrderGenerator] started accepting orders");
    }

    @Override
    public void generatorStopped() {
        System.out.println("[OrderGenerator] stopped");
    }

    @Override
    public void orderGenerated(int orderId) {
        System.out.printf("[OrderGenerator] new order #%d%n", orderId);
    }

    @Override
    public void orderStateChanged(int orderId, String state) {
        System.out.printf("[#%d] %s%n", orderId, state);
    }

    @Override
    public void serializerSaved(int count, String filePath) {
        System.out.printf("[Serializer] Saved %d unfinished orders to %s%n", count, filePath);
    }
}
