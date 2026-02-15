package ru.nsu.krasnyanski.primes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ParallelStreamSolverTest {

    @Test
    void shouldReturnTrueWhenNonPrimeExists() throws InterruptedException {
        PrimeSolver solver = new ParallelStreamSolver();
        int[] array = {2, 3, 5, 7, 4, 11};
        Assertions.assertTrue(solver.hasNonPrime(array));
    }

    @Test
    void shouldReturnFalseWhenAllPrime() throws InterruptedException {
        PrimeSolver solver = new ParallelStreamSolver();
        int[] array = {2, 3, 5, 7, 11, 13};
        Assertions.assertFalse(solver.hasNonPrime(array));
    }

    @Test
    void shouldWorkWithLargePrimeSet() throws InterruptedException {
        PrimeSolver solver = new ParallelStreamSolver();
        int[] array = {101, 103, 107, 109, 113, 127};
        Assertions.assertFalse(solver.hasNonPrime(array));
    }

    @Test
    void shouldThrowExceptionForNullArray() {
        PrimeSolver solver = new ParallelStreamSolver();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> solver.hasNonPrime(null)
        );
    }

    @Test
    void shouldReturnFalseForEmptyArray() throws InterruptedException {
        PrimeSolver solver = new ParallelStreamSolver();

        Assertions.assertFalse(solver.hasNonPrime(new int[]{}));
    }
}