package ru.nsu.krasnyanskii.pizzeria;

/**
 * Entry point and lifecycle manager for the pizzeria simulation.
 *
 * Shutdown strategy: FULL STOP with serialization.
 *  1. OrderGenerator stops — no new orders enter the queue.
 *  2. Remaining orders in the queue are drained and marked CANCELLED.
 *  3. Bakers are interrupted — any order being cooked is lost / serialized.
 *  4. Storage is closed — couriers wake up and exit.
 *  5. All unfinished orders (queue + storage + in-flight) are serialized to JSON.
 */
public class Pizzeria {

    public static void main(String[] args) throws Exception {
        String configPath = args.length > 0 ? args[0] : "config.json";
        String serializedPath = args.length > 1 ? args[1] : "unfinished_orders.json";

        System.out.println("=== Пиццерия открывается! ===");
        PizzeriaConfig config = ConfigLoader.load(configPath);

        // --- Infrastructure ---
        BlockingOrderQueue<Order> orderQueue = new BlockingOrderQueue<>(1000);
        PizzaStorage storage = new PizzaStorage(config.storageCapacity);

        // --- Workers ---
        List<Thread> bakerThreads  = new ArrayList<>();
        List<Baker>  bakers        = new ArrayList<>();
        List<Thread> courierThreads= new ArrayList<>();
        List<Courier> couriers     = new ArrayList<>();

        for (int i = 0; i < config.bakers.size(); i++) {
            PizzeriaConfig.BakerConfig bc = config.bakers.get(i);
            Baker baker = new Baker(i + 1, bc.cookingTimeMs, orderQueue, storage);
            bakers.add(baker);
            Thread t = new Thread(baker, "Baker-" + (i + 1));
            bakerThreads.add(t);
            t.start();
        }

        for (int i = 0; i < config.couriers.size(); i++) {
            PizzeriaConfig.CourierConfig cc = config.couriers.get(i);
            Courier courier = new Courier(i + 1, cc.trunkCapacity, cc.deliveryTimeMs, storage);
            couriers.add(courier);
            Thread t = new Thread(courier, "Courier-" + (i + 1));
            courierThreads.add(t);
            t.start();
        }

        OrderGenerator generator = new OrderGenerator(orderQueue, config.orderIntervalMs);
        Thread generatorThread = new Thread(generator, "OrderGenerator");
        generatorThread.start();

        // --- Work for configured duration ---
        System.out.printf("[Pizzeria] Работаем %d мс...%n", config.workDurationMs);
        Thread.sleep(config.workDurationMs);

        // === SHUTDOWN: Full stop with serialization ===
        System.out.println("\n=== Время вышло! Начинаем остановку... ===");

        // 1. Stop order generator
        generator.stop();
        generatorThread.interrupt();
        generatorThread.join();

        // 2. Close the order queue — bakers will finish current order then exit
        orderQueue.close();

        // 3. Drain queued (unstarted) orders
        List<Order> unfinished = new ArrayList<>(orderQueue.drainAll());
        for (Order o : unfinished) {
            o.setState(Order.State.CANCELLED);
        }

        // 4. Interrupt bakers (stop cooking)
        for (Baker b : bakers) b.stop();
        for (Thread t : bakerThreads) { t.interrupt(); }
        for (Thread t : bakerThreads) { t.join(); }

        // 5. Close storage — couriers will exit
        storage.closeAccepting();
        for (Courier c : couriers) c.stop();
        for (Thread t : courierThreads) { t.interrupt(); }
        for (Thread t : courierThreads) { t.join(); }

        // 6. Drain storage (pizzas ready but not delivered)
        List<Order> inStorage = storage.drainAll();
        for (Order o : inStorage) {
            o.setState(Order.State.CANCELLED);
        }
        unfinished.addAll(inStorage);

        // 7. Serialize
        if (!unfinished.isEmpty()) {
            OrderSerializer.save(unfinished, serializedPath);
        } else {
            System.out.println("[Pizzeria] Все заказы выполнены! Ничего не сериализуется.");
        }

        System.out.println("=== Пиццерия закрыта. До свидания! ===");
    }
}
