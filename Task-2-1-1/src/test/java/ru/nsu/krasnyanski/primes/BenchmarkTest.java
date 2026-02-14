package ru.nsu.krasnyanski.primes;

import org.junit.jupiter.api.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

class BenchmarkTest {

    private static final int ARRAY_SIZE = 200_000;

    private int[] generatePrimeArray(int size) {
        Random r = new Random();
        int[] arr = new int[size];
        int base = 10_000_000;
        for (int i = 0; i < size; i++) {
            arr[i] = base + r.nextInt(1_000_000) | 1;
        }
        return arr;
    }

    @Test
    void benchmarkRunners() throws InterruptedException, IOException {
        int[] data = generatePrimeArray(ARRAY_SIZE);

        try (FileWriter out = new FileWriter("results/timings.csv")) {
            out.write("method,threads,time_ms\n");

            long start = System.currentTimeMillis();
            SequentialSolver.hasNonPrime(data);
            long end = System.currentTimeMillis();
            out.write("sequential,1," + (end - start) + "\n");

            for (int t = 1; t <= Runtime.getRuntime().availableProcessors(); t *= 2) {
                start = System.currentTimeMillis();
                ThreadSolver.hasNonPrime(data, t);
                end = System.currentTimeMillis();
                out.write("threads," + t + "," + (end - start) + "\n");
            }

            start = System.currentTimeMillis();
            ParallelStreamSolver.hasNonPrime(data);
            end = System.currentTimeMillis();
            out.write("parallelStream,-1," + (end - start) + "\n");
        }
    }
}
