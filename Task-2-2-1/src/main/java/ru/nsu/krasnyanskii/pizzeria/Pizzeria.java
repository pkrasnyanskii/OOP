package ru.nsu.krasnyanskii.pizzeria;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import ru.nsu.krasnyanskii.pizzeria.workers.Baker;
import ru.nsu.krasnyanskii.pizzeria.workers.Courier;
import ru.nsu.krasnyanskii.pizzeria.workers.OrderGenerator;

/**
 * Entry point and lifecycle manager for the pizzeria simulation.
 *
 * <p><b>DIP</b>: {@code stopAll} and {@code interruptAndJoin} work with
 * {@link Stoppable}/{@link Thread} abstractions — no coupling to concrete worker types.</p>
 *
 * <p>Shutdown strategy: stop generator → drain queue → wait for bakers →
 * close storage → wait for couriers → serialize unfinished orders.</p>
 */
public class Pizzeria {

    /**
     * Starts the pizzeria.
     *
     * @param args optional: {@code args[0]} config path, {@code args[1]} serialized orders path
     * @throws Exception if config cannot be loaded or threads are interrupted
     */
    public static void main(String[] args) throws Exception {
        String configPath     = args.length > 0 ? args[0] : "config.json";
        String serializedPath = args.length > 1 ? args[1] : "unfinished_orders.json";

        PizzeriaView view = new PizzeriaView();
        view.pizzeriaOpened();
        PizzeriaConfig config = ConfigLoader.load(configPath);

        BlockingOrderQueue<Order> orderQueue = new BlockingOrderQueue<>(1000);
        PizzaStorage storage = new PizzaStorage(config.getStorageCapacity());

        List<Baker>   bakers         = createBakers(config, orderQueue, storage, view);
        List<Courier> couriers       = createCouriers(config, storage, view);
        List<Thread>  bakerThreads   = startThreads(bakers, "Baker");
        List<Thread>  courierThreads = startThreads(couriers, "Courier");

        OrderGenerator generator = new OrderGenerator(
                orderQueue, config.getOrderIntervalMs(), view);
        Thread generatorThread = new Thread(generator, "OrderGenerator");
        generatorThread.start();

        view.workingFor(config.getWorkDurationMs());
        Thread.sleep(config.getWorkDurationMs());
        view.shutdownStarted();

        List<Order> unfinished = shutdown(
                generator, generatorThread,
                bakers, bakerThreads,
                couriers, courierThreads,
                orderQueue, storage);

        if (!unfinished.isEmpty()) {
            OrderSerializer.save(unfinished, serializedPath, view);
        } else {
            view.allOrdersDone();
        }
        view.pizzeriaClosed();
    }

    private static List<Baker> createBakers(PizzeriaConfig config,
                                             BlockingOrderQueue<Order> queue,
                                             PizzaStorage storage,
                                             PizzeriaView view) {
        return IntStream.range(0, config.getBakers().size())
                .mapToObj(i -> new Baker(
                        i + 1,
                        config.getBakers().get(i).getCookingTimeMs(),
                        queue, storage, view))
                .collect(Collectors.toList());
    }

    private static List<Courier> createCouriers(PizzeriaConfig config,
                                                 PizzaStorage storage,
                                                 PizzeriaView view) {
        return IntStream.range(0, config.getCouriers().size())
                .mapToObj(i -> new Courier(
                        i + 1,
                        config.getCouriers().get(i).getTrunkCapacity(),
                        config.getCouriers().get(i).getDeliveryTimeMs(),
                        storage, view))
                .collect(Collectors.toList());
    }

    private static <T extends Runnable> List<Thread> startThreads(List<T> workers,
                                                                    String prefix) {
        return IntStream.range(0, workers.size())
                .mapToObj(i -> {
                    Thread t = new Thread(workers.get(i), prefix + "-" + (i + 1));
                    t.start();
                    return t;
                })
                .collect(Collectors.toList());
    }

    private static List<Order> shutdown(OrderGenerator generator,
                                         Thread generatorThread,
                                         List<Baker> bakers,
                                         List<Thread> bakerThreads,
                                         List<Courier> couriers,
                                         List<Thread> courierThreads,
                                         BlockingOrderQueue<Order> orderQueue,
                                         PizzaStorage storage) throws InterruptedException {
        generator.stop();
        generatorThread.interrupt();
        generatorThread.join();

        orderQueue.close();

        List<Order> unfinished = new ArrayList<>(orderQueue.drainAll());
        unfinished.forEach(o -> o.setState(Order.State.CANCELLED));

        stopAll(bakers);
        interruptAndJoin(bakerThreads);

        storage.closeAccepting();
        stopAll(couriers);
        interruptAndJoin(courierThreads);

        List<Order> inStorage = storage.drainAll();
        inStorage.forEach(o -> o.setState(Order.State.CANCELLED));
        unfinished.addAll(inStorage);

        return unfinished;
    }

    private static void stopAll(List<? extends Stoppable> workers) {
        workers.forEach(Stoppable::stop);
    }

    private static void interruptAndJoin(List<Thread> threads) throws InterruptedException {
        threads.forEach(Thread::interrupt);
        for (Thread t : threads) {
            t.join(); // join() throws checked InterruptedException — cannot use stream here
        }
    }
}
