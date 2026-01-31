package ru.nsu.krasnyanski.primes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class BenchmarkRunner {

    private static final int ARRAY_SIZE = 200_000;

    public static void main(String[] args) throws Exception {
        int[] data = generatePrimeArray(ARRAY_SIZE);

        try (FileWriter out = new FileWriter("results/timings.csv")) {
            out.write("method,threads,time_ms\n");

            measure(out, "sequential", 1,
                    () -> SequentialSolver.hasNonPrime(data));

            for (int t = 1; t <= Runtime.getRuntime().availableProcessors(); t *= 2) {
                int threads = t;
                measure(out, "threads", threads,
                        () -> {
                            try {
                                ThreadSolver.hasNonPrime(data, threads);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
            }

            measure(out, "parallelStream", -1,
                    () -> ParallelStreamSolver.hasNonPrime(data));
        }
    }

    private static void measure(FileWriter out, String name, int threads, Runnable task)
            throws IOException {

        long start = System.currentTimeMillis();
        task.run();
        long end = System.currentTimeMillis();

        out.write(name + "," + threads + "," + (end - start) + "\n");
    }

    private static int[] generatePrimeArray(int size) {
        Random r = new Random();
        int[] arr = new int[size];
        int base = 10_000_000;

        for (int i = 0; i < size; i++) {
            arr[i] = base + r.nextInt(1_000_000) | 1;
        }
        return arr;
    }
}
