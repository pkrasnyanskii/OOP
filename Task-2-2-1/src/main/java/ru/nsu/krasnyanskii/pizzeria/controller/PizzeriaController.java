package ru.nsu.krasnyanskii.pizzeria.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import ru.nsu.krasnyanskii.pizzeria.BlockingOrderQueue;
import ru.nsu.krasnyanskii.pizzeria.PizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.Stoppable;
import ru.nsu.krasnyanskii.pizzeria.config.ConfigLoader;
import ru.nsu.krasnyanskii.pizzeria.config.OrderSerializer;
import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.model.PizzeriaConfig;
import ru.nsu.krasnyanskii.pizzeria.view.ConsolePizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.workers.Baker;
import ru.nsu.krasnyanskii.pizzeria.workers.Courier;
import ru.nsu.krasnyanskii.pizzeria.workers.OrderGenerator;

/**
 * Orchestrates the full pizzeria lifecycle.
 *
 * <p>Shutdown strategy: stop generator → drain queue → wait for bakers →
 * close storage → wait for couriers → serialize unfinished orders.</p>
 */
public class PizzeriaController {

    private final String configPath;
    private final String serializedPath;

    /**
     * Creates a controller from command-line arguments.
     *
     * @param args optional: {@code args[0]} config path, {@code args[1]} serialized orders path
     */
    public PizzeriaController(String[] args) {
        this.configPath     = args.length > 0 ? args[0] : "config.json";
        this.serializedPath = args.length > 1 ? args[1] : "unfinished_orders.json";
    }

    /**
     * Starts the pizzeria, runs for the configured duration, then shuts down gracefully.
     *
     * @throws Exception if config cannot be loaded or threads are interrupted
     */
    public void run() throws Exception {
        PizzeriaView view = new ConsolePizzeriaView();
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

    /**
     * Creates baker instances from configuration.
     *
     * @param config     pizzeria configuration
     * @param queue      shared order queue
     * @param storage    shared pizza storage
     * @param view       view for console output
     * @return list of configured bakers
     */
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

    /**
     * Creates courier instances from configuration.
     *
     * @param config  pizzeria configuration
     * @param storage shared pizza storage
     * @param view    view for console output
     * @return list of configured couriers
     */
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

    /**
     * Wraps each worker in a named thread and starts it.
     *
     * @param workers list of runnable workers
     * @param prefix  thread name prefix (e.g. {@code "Baker"})
     * @param <T>     worker type
     * @return list of started threads
     */
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

    /**
     * Performs a graceful shutdown and collects unfinished orders.
     *
     * @param generator       order generator to stop first
     * @param generatorThread generator thread to join
     * @param bakers          baker workers
     * @param bakerThreads    baker threads
     * @param couriers        courier workers
     * @param courierThreads  courier threads
     * @param orderQueue      shared order queue
     * @param storage         shared pizza storage
     * @return list of orders that could not be completed
     * @throws InterruptedException if the current thread is interrupted while joining
     */
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

    /**
     * Calls {@link Stoppable#stop()} on every worker.
     *
     * @param workers list of stoppable workers
     */
    private static void stopAll(List<? extends Stoppable> workers) {
        workers.forEach(Stoppable::stop);
    }

    /**
     * Interrupts each thread and waits for it to finish.
     *
     * @param threads list of threads to interrupt and join
     * @throws InterruptedException if the current thread is interrupted while joining
     */
    private static void interruptAndJoin(List<Thread> threads) throws InterruptedException {
        threads.forEach(Thread::interrupt);
        for (Thread t : threads) {
            t.join();
        }
    }
}
