package ru.nsu.krasnyanski.primes;

/**
 * Multi-threaded implementation of {@link PrimeSolver}.
 *
 * <p>Splits the input array into chunks and processes each chunk
 * in a separate thread. Uses {@link MyAtomicBoolean} — a hand-written
 * thread-safe flag — to signal early termination across threads.</p>
 *
 * <p>Three ways to create a thread in Java:
 * <ol>
 *   <li>Extend {@link Thread} and override {@code run()}</li>
 *   <li>Pass a {@link Runnable} to {@code new Thread(runnable)} — used here</li>
 *   <li>Submit a task to {@link java.util.concurrent.ExecutorService}</li>
 * </ol>
 * </p>
 *
 * <p>{@code thread.start()} creates a new OS thread and returns immediately.
 * {@code thread.run()} just calls the method in the current thread — no parallelism.</p>
 */
public class ThreadSolver implements PrimeSolver {

    private final int threadCount;

    /**
     * Constructs a solver with the given number of threads.
     *
     * @param threadCount number of worker threads (must be >= 1)
     * @throws IllegalArgumentException if threadCount is less than 1
     */
    public ThreadSolver(int threadCount) {
        if (threadCount < 1) {
            throw new IllegalArgumentException(
                    "Thread count must be greater than zero"
            );
        }
        this.threadCount = threadCount;
    }

    @Override
    public boolean hasNonPrime(int[] array) throws InterruptedException {
        if (array == null) {
            throw new IllegalArgumentException("Array must not be null");
        }
        if (array.length == 0) {
            return false;
        }

        // Наш собственный потокобезопасный флаг вместо AtomicBoolean
        MyAtomicBoolean found = new MyAtomicBoolean(false);
        Thread[] threads = new Thread[threadCount];

        int chunkSize = array.length / threadCount;
        int remainder = array.length % threadCount;
        int start = 0;

        for (int i = 0; i < threadCount; i++) {
            int currentChunk = chunkSize + (i < remainder ? 1 : 0);
            int end = start + currentChunk;

            final int localStart = start;
            final int localEnd = end;

            // Способ 2: передаём Runnable (лямбду) в конструктор Thread
            threads[i] = new Thread(() -> {
                for (int j = localStart; j < localEnd; j++) {
                    if (found.get()) {
                        return; // другой поток уже нашёл непростое
                    }
                    if (!PrimeChecker.isPrime(array[j])) {
                        found.set(true);
                        return;
                    }
                }
            });

            // start() — запускает НОВЫЙ поток в JVM
            // run()   — просто вызвал бы метод в текущем потоке, без параллелизма
            threads[i].start();
            start = end;
        }

        for (Thread thread : threads) {
            thread.join(); // ждём завершения каждого потока
        }

        return found.get();
    }

    @Override
    public String getName() {
        return "ThreadSolver(" + threadCount + " threads)";
    }
}