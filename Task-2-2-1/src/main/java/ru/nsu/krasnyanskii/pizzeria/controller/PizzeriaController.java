package ru.nsu.krasnyanskii.pizzeria.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import ru.nsu.krasnyanskii.pizzeria.config.ConfigLoader;
import ru.nsu.krasnyanskii.pizzeria.config.OrderSerializer;
import ru.nsu.krasnyanskii.pizzeria.model.Order;
import ru.nsu.krasnyanskii.pizzeria.model.PizzeriaConfig;
import ru.nsu.krasnyanskii.pizzeria.queue.BlockingOrderQueue;
import ru.nsu.krasnyanskii.pizzeria.queue.OrderQueue;
import ru.nsu.krasnyanskii.pizzeria.storage.BoundedPizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.storage.PizzaStorage;
import ru.nsu.krasnyanskii.pizzeria.view.BakerView;
import ru.nsu.krasnyanskii.pizzeria.view.ConsolePizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.view.CourierView;
import ru.nsu.krasnyanskii.pizzeria.view.PizzeriaView;
import ru.nsu.krasnyanskii.pizzeria.workers.Baker;
import ru.nsu.krasnyanskii.pizzeria.workers.Courier;
import ru.nsu.krasnyanskii.pizzeria.workers.OrderGenerator;
import ru.nsu.krasnyanskii.pizzeria.workers.Worker;

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

        OrderQueue<Order> orderQueue = new BlockingOrderQueue<>(1000);
        PizzaStorage storage = new BoundedPizzaStorage(config.getStorageCapacity());

        List<Worker> bakers         = createBakers(config, orderQueue, storage, view);
        List<Worker> couriers       = createCouriers(config, storage, view);
        List<Thread> bakerThreads   = startThreads(bakers, "Baker");
        List<Thread> courierThreads = startThreads(couriers, "Courier");

        Worker generator = new OrderGenerator(orderQueue, config.getOrderIntervalMs(), view);
        Thread generatorThread = new Thread(generator, "OrderGenerator");
        generatorThread.start();

        view.workingFor(config.getWorkDurationMs());
        Thread.sleep(config.getWorkDurationMs());
        view.shutdownStarted();

        List<Order> unfinished = shutdown(
                generatorThread,
                bakerThreads,
                courierThreads,
                orderQueue, storage);

        if (!unfinished.isEmpty()) {
            OrderSerializer.save(unfinished, serializedPath);
            view.serializerSaved(unfinished.size(), serializedPath);
        } else {
            view.allOrdersDone();
        }
        view.pizzeriaClosed();
    }

    /**
     * Creates baker workers from configuration.
     *
     * @param config  pizzeria configuration
     * @param queue   shared order queue
     * @param storage shared pizza storage
     * @param view    view for console output
     * @return list of configured baker workers
     */
    private static List<Worker> createBakers(PizzeriaConfig config,
                                              OrderQueue<Order> queue,
                                              PizzaStorage storage,
                                              BakerView view) {
        return IntStream.range(0, config.getBakers().size())
                .mapToObj(i -> (Worker) new Baker(
                        i + 1,
                        config.getBakers().get(i).getCookingTimeMs(),
                        queue, storage, view))
                .collect(Collectors.toList());
    }

    /**
     * Creates courier workers from configuration.
     *
     * @param config  pizzeria configuration
     * @param storage shared pizza storage
     * @param view    view for console output
     * @return list of configured courier workers
     */
    private static List<Worker> createCouriers(PizzeriaConfig config,
                                                PizzaStorage storage,
                                                CourierView view) {
        return IntStream.range(0, config.getCouriers().size())
                .mapToObj(i -> (Worker) new Courier(
                        i + 1,
                        config.getCouriers().get(i).getTrunkCapacity(),
                        config.getCouriers().get(i).getDeliveryTimeMs(),
                        storage, view))
                .collect(Collectors.toList());
    }

    /**
     * Wraps each worker in a named thread and starts it.
     *
     * @param workers list of workers to start
     * @param prefix  thread name prefix (e.g. {@code "Baker"})
     * @return list of started threads
     */
    private static List<Thread> startThreads(List<Worker> workers, String prefix) {
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
     * <p>Stops the generator first (so no new orders appear), closes the queue
     * so bakers exit when it drains, then closes storage so couriers exit when it drains.
     * Each phase finishes by interrupting and joining the corresponding threads.</p>
     *
     * @param generatorThread generator thread to interrupt and join
     * @param bakerThreads    baker threads to interrupt and join
     * @param courierThreads  courier threads to interrupt and join
     * @param orderQueue      shared order queue
     * @param storage         shared pizza storage
     * @return list of orders that could not be completed
     * @throws InterruptedException if the current thread is interrupted while joining
     */
    private static List<Order> shutdown(Thread generatorThread,
                                         List<Thread> bakerThreads,
                                         List<Thread> courierThreads,
                                         OrderQueue<Order> orderQueue,
                                         PizzaStorage storage) throws InterruptedException {
        generatorThread.interrupt();
        generatorThread.join();

        orderQueue.close();

        List<Order> unfinished = new ArrayList<>(orderQueue.drainAll());
        unfinished.forEach(o -> o.setState(Order.State.CANCELLED));

        interruptAndJoin(bakerThreads);

        storage.closeAccepting();
        interruptAndJoin(courierThreads);

        List<Order> inStorage = storage.drainAll();
        inStorage.forEach(o -> o.setState(Order.State.CANCELLED));
        unfinished.addAll(inStorage);

        return unfinished;
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
