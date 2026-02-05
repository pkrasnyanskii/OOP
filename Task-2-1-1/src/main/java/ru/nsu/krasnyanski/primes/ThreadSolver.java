package ru.nsu.krasnyanski.primes;

import java.util.concurrent.atomic.AtomicBoolean;

public class ThreadSolver {

    public static boolean hasNonPrime(int[] array, int threadCount)
            throws InterruptedException {

        AtomicBoolean found = new AtomicBoolean(false);
        Thread[] threads = new Thread[threadCount];

        int chunk = array.length / threadCount;

        for (int t = 0; t < threadCount; t++) {
            int start = t * chunk;
            int end = (t == threadCount - 1)
                    ? array.length
                    : start + chunk;

            threads[t] = new Thread(() -> {
                for (int i = start; i < end && !found.get(); i++) {
                    if (!PrimeChecker.isPrime(array[i])) {
                        found.set(true);
                        break;
                    }
                }
            });
            threads[t].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        return found.get();
    }
}
