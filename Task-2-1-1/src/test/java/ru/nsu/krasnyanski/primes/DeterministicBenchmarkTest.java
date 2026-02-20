package ru.nsu.krasnyanski.primes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class DeterministicBenchmarkTest {

    private static final int ARRAY_SIZE = 200_000;

    @Test
    void runAllSolversAndWriteCsv() throws Exception {
        int[] data = generateDeterministicData(ARRAY_SIZE);

        List<PrimeSolver> solvers = new ArrayList<>();
        solvers.add(new SequentialSolver());
        solvers.add(new ThreadSolver(1));
        solvers.add(new ThreadSolver(2));
        solvers.add(new ThreadSolver(4));
        solvers.add(new ParallelStreamSolver());

        try (FileWriter writer = new FileWriter("results/timings.csv")) {
            writer.write("solver,time_ms\n");

            for (PrimeSolver solver : solvers) {
                long start = System.currentTimeMillis();
                solver.hasNonPrime(data);
                long end = System.currentTimeMillis();

                writer.write(solver.getName() + "," + (end - start) + "\n");
            }
        }
    }

    private int[] generateDeterministicData(int size) {
        int[] array = new int[size];
        int base = 10_000_000;

        for (int i = 0; i < size; i++) {
            array[i] = base + i * 2 + 1;
        }

        return array;
    }
}