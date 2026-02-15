package ru.nsu.krasnyanski.primes;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SequentialSolverTest {

    @Test
    void shouldReturnTrueWhenNonPrimeExists() throws InterruptedException {
        PrimeSolver solver = new SequentialSolver();
        int[] array = {6, 8, 7, 13, 5, 9, 4};
        Assertions.assertTrue(solver.hasNonPrime(array));
    }

    @Test
    void shouldReturnFalseWhenAllPrime() throws InterruptedException {
        PrimeSolver solver = new SequentialSolver();
        int[] array = {2, 3, 5, 7, 11, 13, 17};
        Assertions.assertFalse(solver.hasNonPrime(array));
    }

    @Test
    void shouldWorkWithSingleElement() throws InterruptedException {
        PrimeSolver solver = new SequentialSolver();
        Assertions.assertTrue(solver.hasNonPrime(new int[]{4}));
        Assertions.assertFalse(solver.hasNonPrime(new int[]{5}));
    }

    @Test
    void shouldWorkWithEmptyArray() throws InterruptedException {
        PrimeSolver solver = new SequentialSolver();
        Assertions.assertFalse(solver.hasNonPrime(new int[]{}));
    }

    @Test
    void shouldThrowExceptionForNullArray() {
        PrimeSolver solver = new SequentialSolver();

        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> solver.hasNonPrime(null)
        );
    }

    @Test
    void shouldReturnFalseForEmptyArray() throws InterruptedException {
        PrimeSolver solver = new SequentialSolver();

        Assertions.assertFalse(solver.hasNonPrime(new int[]{}));
    }
}