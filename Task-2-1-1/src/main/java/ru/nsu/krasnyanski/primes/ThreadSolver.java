package ru.nsu.krasnyanski.primes;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Multi-threaded implementation of {@link PrimeSolver}.
 *
 * <p>Splits the input array into chunks and processes them
 * using a specified number of threads.</p>
 */
public class ThreadSolver implements PrimeSolver {

    private final int threadCount;

    /**
     * Constructs a solver with a specified number of threads.
     *
     * @param threadCount number of worker threads
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

        AtomicBoolean found = new AtomicBoolean(false);
        Thread[] threads = new Thread[threadCount];

        int chunkSize = array.length / threadCount;
        int remainder = array.length % threadCount;

        int start = 0;

        for (int i = 0; i < threadCount; i++) {
            int currentChunk = chunkSize + (i < remainder ? 1 : 0);
            int end = start + currentChunk;

            final int localStart = start;
            final int localEnd = end;

            threads[i] = new Thread(() -> {
                for (int j = localStart; j < localEnd && !found.get(); j++) {
                    if (!PrimeChecker.isPrime(array[j])) {
                        found.set(true);
                        break;
                    }
                }
            });

            threads[i].start();
            start = end;
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return found.get();
    }

    @Override
    public String getName() {
        return "ThreadSolver(" + threadCount + ")";
    }
}