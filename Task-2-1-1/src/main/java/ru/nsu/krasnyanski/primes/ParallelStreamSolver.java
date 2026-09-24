package ru.nsu.krasnyanski.primes;

import java.util.Arrays;

/**
 * Parallel stream implementation of {@link PrimeSolver}.
 */
public class ParallelStreamSolver implements PrimeSolver {

    @Override
    public boolean hasNonPrime(int[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array must not be null");
        }

        if (array.length == 0) {
            return false;
        }

        return Arrays.stream(array)
                .parallel()
                .anyMatch(value -> !PrimeChecker.isPrime(value));
    }

    @Override
    public String getName() {
        return "ParallelStream";
    }
}