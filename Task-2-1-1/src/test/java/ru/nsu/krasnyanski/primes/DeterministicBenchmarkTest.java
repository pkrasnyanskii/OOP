package ru.nsu.krasnyanski.primes;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Deterministic benchmark comparing all three solver implementations.
 *
 * <p>Test data: 200 000 copies of the prime number 9_999_991.
 * Why this number? sqrt(9_999_991) ≈ 3162, so each primality check
 * requires ~1581 iterations — enough CPU work to make parallelism measurable.
 * All elements are identical and prime, so no solver can short-circuit early.
 * The array is fully deterministic: same data on every run, on every machine.</p>
 *
 * <p>Results are written to results/timings.csv for plotting.</p>
 */
class DeterministicBenchmarkTest {

    // 9_999_991 — простое число (проверено заранее)
    // Все элементы одинаковые и простые → ни один solver не завершится досрочно
    private static final int PRIME_VALUE = 9_999_991;
    private static final int ARRAY_SIZE = 200_000;

    @Test
    void runAllSolversAndWriteCsv() throws Exception {
        int[] data = buildArray(ARRAY_SIZE, PRIME_VALUE);

        List<PrimeSolver> solvers = new ArrayList<>();
        solvers.add(new SequentialSolver());
        solvers.add(new ThreadSolver(1));
        solvers.add(new ThreadSolver(2));
        solvers.add(new ThreadSolver(4));
        solvers.add(new ParallelStreamSolver());

        Files.createDirectories(Paths.get("results"));

        try (FileWriter writer = new FileWriter("results/timings.csv")) {
            writer.write("solver,time_ms\n");

            for (PrimeSolver solver : solvers) {
                long start = System.currentTimeMillis();
                solver.hasNonPrime(data);
                long elapsed = System.currentTimeMillis() - start;

                String line = solver.getName() + "," + elapsed + "\n";
                writer.write(line);
                System.out.print(line); // дублируем в консоль
            }
        }

        System.out.println("\nРезультаты записаны в results/timings.csv");
    }

    private static int[] buildArray(int size, int value) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = value;
        }
        return array;
    }
}