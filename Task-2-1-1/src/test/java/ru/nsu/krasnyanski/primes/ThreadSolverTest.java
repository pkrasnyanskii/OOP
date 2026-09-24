package ru.nsu.krasnyanski.primes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ThreadSolverTest {

    @Test
    void shouldReturnTrueWhenNonPrimeExists() throws InterruptedException {
        PrimeSolver solver = new ThreadSolver(4);
        int[] array = {2, 3, 5, 7, 4, 11};
        Assertions.assertTrue(solver.hasNonPrime(array));
    }

    @Test
    void shouldReturnFalseWhenAllPrime() throws InterruptedException {
        PrimeSolver solver = new ThreadSolver(3);
        int[] array = {2, 3, 5, 7, 11, 13};
        Assertions.assertFalse(solver.hasNonPrime(array));
    }

    @Test
    void shouldThrowExceptionForInvalidThreadCount() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new ThreadSolver(0)
        );
    }

    @Test
    void shouldThrowExceptionForNullArray() {
        PrimeSolver solver = new ThreadSolver(2);

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> solver.hasNonPrime(null)
        );
    }

    @Test
    void shouldReturnFalseForEmptyArray() throws InterruptedException {
        PrimeSolver solver = new ThreadSolver(2);
        Assertions.assertFalse(solver.hasNonPrime(new int[]{}));
    }

    @Test
    void shouldWorkWhenThreadCountGreaterThanArraySize()
            throws InterruptedException {

        PrimeSolver solver = new ThreadSolver(10);
        int[] array = {2, 3, 5};

        Assertions.assertFalse(solver.hasNonPrime(array));
    }
}