package ru.nsu.krasnyanskii.pizzeria;

import java.util.ArrayList;
import java.util.List;

/**
 * Точка входа и менеджер жизненного цикла пиццерии.
 *
 * <p>Стратегия завершения: ПОЛНАЯ ОСТАНОВКА С СЕРИАЛИЗАЦИЕЙ.
 *   OrderGenerator останавливается — новые заказы не принимаются.
 *   Очередь закрывается — пекари допекают текущие заказы и завершают работу.
 *   Незапущенные заказы из очереди сериализуются.
 *   Склад закрывается — курьеры развозят оставшееся и завершают работу.
 *   Пиццы оставшиеся на складе сериализуются.
 * </p>
 */
public class Pizzeria {

    /**
     * Entry point. Loads config, starts all workers, runs for the configured duration,
     * then performs a full shutdown with serialization of unfinished orders.
     *
     * @param args optional: args[0] = config path, args[1] = serialized orders path
     * @throws Exception if config cannot be loaded or threads are interrupted
     */
    public static void main(String[] args) throws Exception {
        String configPath      = args.length > 0 ? args[0] : "config.json";
        String serializedPath  = args.length > 1 ? args[1] : "unfinished_orders.json";

        System.out.println("=== Пиццерия открывается! ===");
        PizzeriaConfig config = ConfigLoader.load(configPath);

        // --- Инфраструктура ---
        BlockingOrderQueue<Order> orderQueue = new BlockingOrderQueue<>(1000);
        PizzaStorage storage = new PizzaStorage(config.storageCapacity);

        // --- Пекари ---
        List<Baker>  bakers       = new ArrayList<>();
        List<Thread> bakerThreads = new ArrayList<>();
        for (int i = 0; i < config.bakers.size(); i++) {
            PizzeriaConfig.BakerConfig bc = config.bakers.get(i);
            Baker baker = new Baker(i + 1, bc.cookingTimeMs, orderQueue, storage);
            bakers.add(baker);
            Thread t = new Thread(baker, "Baker-" + (i + 1));
            bakerThreads.add(t);
            t.start();
        }

        // --- Курьеры ---
        List<Courier> couriers       = new ArrayList<>();
        List<Thread>  courierThreads = new ArrayList<>();
        for (int i = 0; i < config.couriers.size(); i++) {
            PizzeriaConfig.CourierConfig cc = config.couriers.get(i);
            Courier courier = new Courier(i + 1, cc.trunkCapacity, cc.deliveryTimeMs, storage);
            couriers.add(courier);
            Thread t = new Thread(courier, "Courier-" + (i + 1));
            courierThreads.add(t);
            t.start();
        }

        // --- Генератор заказов ---
        OrderGenerator generator = new OrderGenerator(orderQueue, config.orderIntervalMs);
        Thread generatorThread = new Thread(generator, "OrderGenerator");
        generatorThread.start();

        // --- Работаем заданное время ---
        System.out.printf("[Pizzeria] Работаем %d мс...%n", config.workDurationMs);
        Thread.sleep(config.workDurationMs);

        // === ОСТАНОВКА ===
        System.out.println("\n=== Время вышло! Начинаем остановку... ===");

        // 1. Останавливаем генератор
        generator.stop();
        generatorThread.interrupt();
        generatorThread.join();

        // 2. Закрываем очередь — пекари допекут и выйдут
        orderQueue.close();

        // 3. Забираем незапущенные заказы из очереди
        List<Order> unfinished = new ArrayList<>(orderQueue.drainAll());
        for (Order o : unfinished) {
            o.setState(Order.State.CANCELLED);
        }

        // 4. Ждём завершения пекарей
        for (Baker b : bakers) {
            b.stop();
        }
        for (Thread t : bakerThreads) {
            t.interrupt();
        }
        for (Thread t : bakerThreads) {
            t.join();
        }

        // 5. Закрываем склад — курьеры развезут оставшееся и выйдут
        storage.closeAccepting();
        for (Courier c : couriers) {
            c.stop();
        }
        for (Thread t : courierThreads) {
            t.interrupt();
        }
        for (Thread t : courierThreads) {
            t.join();
        }

        // 6. Забираем пиццы оставшиеся на складе
        List<Order> inStorage = storage.drainAll();
        for (Order o : inStorage) {
            o.setState(Order.State.CANCELLED);
        }
        unfinished.addAll(inStorage);

        // 7. Сериализуем незавершённые заказы
        if (!unfinished.isEmpty()) {
            OrderSerializer.save(unfinished, serializedPath);
        } else {
            System.out.println("[Pizzeria] Все заказы выполнены!");
        }

        System.out.println("=== Пиццерия закрыта. До свидания! ===");
    }
}